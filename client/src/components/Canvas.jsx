import React, { useRef, useState, useEffect, useCallback } from 'react'
import { getEventCoordinates } from '../utils/helpers'
import './Canvas.css'

const VIRTUAL_WIDTH = 10
const VIRTUAL_HEIGHT = 16
const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT

const Canvas = ({ client, color, userID, roomId, isDrawingAllowed, brushSize }) => {
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
  }, [])

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
    const subscription = client.subscribe(`/topic/room/${roomId}/drawing`, (msg) => {
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
        if (lastPos) {
          ctx.beginPath()
          ctx.moveTo(lastPos.x, lastPos.y)
        } else {
          ctx.beginPath()
          ctx.moveTo(offsetX, offsetY)
        }
        ctx.lineTo(offsetX, offsetY)
        ctx.stroke()
        lastPositions.current[data.userID] = { x: offsetX, y: offsetY }
      } else if (data.eventType === 'STOP') {
        delete lastPositions.current[data.userID]
      }
    })
    return () => subscription.unsubscribe()
  }, [client, roomId])

  useEffect(() => {
    if (!client || !client.connected) return
    const clearSubscription = client.subscribe(`/topic/room/${roomId}/clearCanvas`, (msg) => {
      const canvas = canvasRef.current
      if (canvas) {
        const ctx = canvas.getContext('2d')
        ctx.clearRect(0, 0, canvas.width, canvas.height)
      }
    })
    return () => clearSubscription.unsubscribe()
  }, [client, roomId])

  const startDrawing = (event) => {
    event.preventDefault()
    if (!isDrawingAllowed || !client) return
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

  const draw = (event) => {
    if (!isDrawing || !isDrawingAllowed || !client) return
    event.preventDefault()
    const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
    const { width, height } = canvasRef.current
    const normX = (offsetX / width) * VIRTUAL_WIDTH
    const normY = (offsetY / height) * VIRTUAL_HEIGHT
    const ctx = canvasRef.current.getContext('2d')
    ctx.lineTo(offsetX, offsetY)
    ctx.stroke()
    lastPositions.current[userID] = { x: offsetX, y: offsetY }
    const message = {
      normX,
      normY,
      brushSize,
      userID: String(userID),
      eventType: 'DRAW'
    }
    client.publish({ destination: `/app/room/${roomId}/draw`, body: JSON.stringify(message) })
  }

  const stopDrawing = (event) => {
    event.preventDefault()
    if (!isDrawingAllowed || !client) return
    setIsDrawing(false)
    delete lastPositions.current[userID]
    const message = { userID: String(userID), eventType: 'STOP' }
    client.publish({ destination: `/app/room/${roomId}/stopDrawing`, body: JSON.stringify(message) })
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
