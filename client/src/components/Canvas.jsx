import React, { useRef, useState, useEffect, useCallback } from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { setIsFillMode } from '../store/drawSlice'
import { floodFill } from '../utils/floodFill'
import { getEventCoordinates } from '../utils/helpers'
import './Canvas.css'

const VIRTUAL_WIDTH = 10
const VIRTUAL_HEIGHT = 16
const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT

function Canvas({ client, userID, roomId }) {
  const color = useSelector(state => state.draw.color)
  const brushSize = useSelector(state => state.draw.brushSize)
  const isFillMode = useSelector(state => state.draw.isFillMode)
  const isDrawingAllowed = useSelector(state => state.game.isDrawingAllowed)
  const dispatch = useDispatch()
  const canvasRef = useRef(null)
  const lastPositions = useRef({})
  const [isDrawing, setIsDrawing] = useState(false)

  const resizeCanvas = useCallback(() => {
    const canvas = canvasRef.current
    if (!canvas) return
    const ctx = canvas.getContext('2d')
    const maxWidth = window.innerWidth * 0.9
    const maxHeight = window.innerHeight * 0.8
    let width, height
    if (maxWidth / maxHeight > ASPECT_RATIO) {
      height = maxHeight
      width = height * ASPECT_RATIO
    } else {
      width = maxWidth
      height = width / ASPECT_RATIO
    }
    width = Math.min(width, maxWidth)
    height = Math.min(height, maxHeight)
    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
    canvas.width = width
    canvas.height = height
    ctx.putImageData(imageData, 0, 0)
    ctx.lineWidth = 2
    ctx.lineCap = 'round'
  }, [])

  useEffect(() => {
    resizeCanvas()
  }, [resizeCanvas])

  useEffect(() => {
    const canvas = canvasRef.current
    if (canvas) {
      const ctx = canvas.getContext('2d')
      ctx.strokeStyle = color
    }
  }, [color])

  useEffect(() => {
    const canvas = canvasRef.current
    if (canvas) {
      const ctx = canvas.getContext('2d')
      ctx.lineWidth = brushSize
    }
  }, [brushSize])

  useEffect(() => {
    if (!client || !client.connected) return
    const sub = client.subscribe(`/topic/room/${roomId}/drawing`, msg => {
      const data = JSON.parse(msg.body)
      const canvas = canvasRef.current
      if (!canvas) return
      const ctx = canvas.getContext('2d')
      const { width, height } = canvas
      const offsetX = (data.normX / VIRTUAL_WIDTH) * width
      const offsetY = (data.normY / VIRTUAL_HEIGHT) * height
      if (data.eventType === 'START') {
        ctx.lineWidth = data.brushSize
        ctx.beginPath()
        ctx.moveTo(offsetX, offsetY)
        ctx.strokeStyle = data.color
        lastPositions.current[data.userID] = { x: offsetX, y: offsetY }
      } else if (data.eventType === 'DRAW') {
        ctx.lineWidth = data.brushSize
        const lastPos = lastPositions.current[data.userID]
        ctx.beginPath()
        if (lastPos) {
          ctx.moveTo(lastPos.x, lastPos.y)
        } else {
          ctx.moveTo(offsetX, offsetY)
        }
        ctx.lineTo(offsetX, offsetY)
        ctx.stroke()
        lastPositions.current[data.userID] = { x: offsetX, y: offsetY }
      } else if (data.eventType === 'STOP') {
        delete lastPositions.current[data.userID]
      } else if (data.eventType === 'FILL') {
        floodFill(ctx, Math.floor(offsetX), Math.floor(offsetY), data.color)
      }
    })
    return () => sub.unsubscribe()
  }, [client, roomId])

  useEffect(() => {
    if (!client || !client.connected) return
    const clearSub = client.subscribe(`/topic/room/${roomId}/clearCanvas`, () => {
      const canvas = canvasRef.current
      if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.clearRect(0, 0, canvas.width, canvas.height)
      }
    })
    return () => clearSub.unsubscribe()
  }, [client, roomId])

  function startDrawing(event) {
    event.preventDefault()
    if (!isDrawingAllowed || !client) return
    if (isFillMode) {
      const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
      const { width, height } = canvasRef.current
      const normX = (offsetX / width) * VIRTUAL_WIDTH
      const normY = (offsetY / height) * VIRTUAL_HEIGHT
      const msg = {
        normX,
        normY,
        color,
        userID: String(userID),
        eventType: 'FILL'
      }
      client.publish({ destination: `/app/room/${roomId}/fill`, body: JSON.stringify(msg) })
      dispatch(setIsFillMode(false))
      return
    }
    const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
    const { width, height } = canvasRef.current
    const normX = (offsetX / width) * VIRTUAL_WIDTH
    const normY = (offsetY / height) * VIRTUAL_HEIGHT
    const ctx = canvasRef.current.getContext('2d')
    ctx.beginPath()
    ctx.moveTo(offsetX, offsetY)
    ctx.strokeStyle = color
    setIsDrawing(true)
    lastPositions.current[userID] = { x: offsetX, y: offsetY }
    const message = {
      normX,
      normY,
      color,
      brushSize,
      userID: String(userID),
      eventType: 'START'
    }
    client.publish({ destination: `/app/room/${roomId}/startDrawing`, body: JSON.stringify(message) })
  }

  function draw(event) {
    if (!isDrawing || !isDrawingAllowed || !client || isFillMode) return
    event.preventDefault()
    const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
    const { width, height } = canvasRef.current
    const normX = (offsetX / width) * VIRTUAL_WIDTH
    const normY = (offsetY / height) * VIRTUAL_HEIGHT
    const ctx = canvasRef.current.getContext('2d')
    ctx.lineTo(offsetX, offsetY)
    ctx.stroke()
    lastPositions.current[userID] = { x: offsetX, y: offsetY }
    const msg = {
      normX,
      normY,
      brushSize,
      userID: String(userID),
      eventType: 'DRAW'
    }
    client.publish({ destination: `/app/room/${roomId}/draw`, body: JSON.stringify(msg) })
  }

  function stopDrawing(event) {
    event.preventDefault()
    if (!isDrawingAllowed || !client) return
    setIsDrawing(false)
    delete lastPositions.current[userID]
    const msg = { userID: String(userID), eventType: 'STOP' }
    client.publish({ destination: `/app/room/${roomId}/stopDrawing`, body: JSON.stringify(msg) })
  }

  return (
    <canvas
      ref={canvasRef}
      className="myCanvas"
      onMouseDown={startDrawing}
      onMouseMove={draw}
      onMouseUp={stopDrawing}
      onMouseLeave={stopDrawing}
      onTouchStart={startDrawing}
      onTouchMove={draw}
      onTouchEnd={stopDrawing}
    />
  )
}

export default Canvas
