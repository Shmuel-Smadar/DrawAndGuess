import { useCallback } from 'react'
import { setIsFillMode } from '../store/drawSlice'
import { getEventCoordinates } from '../utils/helpers'
import {
  VIRTUAL_WIDTH,
  VIRTUAL_HEIGHT,
  EVENT_TYPE_START,
  EVENT_TYPE_DRAW,
  EVENT_TYPE_STOP,
  EVENT_TYPE_FILL
} from '../utils/constants'
import { APP_ROOM_FILL, APP_ROOM_START_DRAWING, APP_ROOM_DRAW, APP_ROOM_STOP_DRAWING } from '../utils/subscriptionConstants'

export const useDrawingHandlers = ({
  client,
  userID,
  roomId,
  canvasRef,
  isFillMode,
  isDrawingAllowed,
  color,
  brushSize,
  dispatch,
  setIsDrawing,
  lastPositions,
  isDrawing
}) => {
  const computeCoords = useCallback(
    (event) => {
      const { offsetX, offsetY } = getEventCoordinates(event, canvasRef)
      const { width, height } = canvasRef.current
      const normX = (offsetX / width) * VIRTUAL_WIDTH
      const normY = (offsetY / height) * VIRTUAL_HEIGHT
      return { offsetX, offsetY, normX, normY }
    },
    [canvasRef]
  )

  const startDrawing = useCallback(
    (event) => {
      if (!isDrawingAllowed || !client) return
      const ctx = canvasRef.current.getContext('2d')
      if (isFillMode) {
        const { normX, normY } = computeCoords(event)
        const msg = {
          normX,
          normY,
          color,
          userID: String(userID),
          eventType: EVENT_TYPE_FILL
        }

        client.publish({
          destination: APP_ROOM_FILL(roomId),
          body: JSON.stringify(msg)
        })

        dispatch(setIsFillMode(false))
        return
      }

      const { offsetX, offsetY, normX, normY } = computeCoords(event)
      ctx.beginPath()
      ctx.moveTo(offsetX, offsetY)
      ctx.lineTo(offsetX, offsetY)
      ctx.strokeStyle = color
      ctx.stroke()
      setIsDrawing(true)
      lastPositions.current[userID] = { x: offsetX, y: offsetY }

      const message = {
        normX,
        normY,
        color,
        brushSize,
        userID: String(userID),
        eventType: EVENT_TYPE_START
      }
      client.publish({
        destination: APP_ROOM_START_DRAWING(roomId),
        body: JSON.stringify(message)
      })
    },
    [
      isDrawingAllowed,
      client,
      isFillMode,
      color,
      brushSize,
      roomId,
      userID,
      canvasRef,
      dispatch,
      setIsDrawing,
      lastPositions,
      computeCoords
    ]
  )

  const draw = useCallback(
    (event) => {
      if (!isDrawing || !isDrawingAllowed || !client || isFillMode) return
      const { offsetX, offsetY, normX, normY } = computeCoords(event)
      const ctx = canvasRef.current.getContext('2d')
      ctx.lineTo(offsetX, offsetY)
      ctx.stroke()
      lastPositions.current[userID] = { x: offsetX, y: offsetY }
      const msg = {
        normX,
        normY,
        brushSize,
        userID: String(userID),
        eventType: EVENT_TYPE_DRAW
      }

      client.publish({
        destination: APP_ROOM_DRAW(roomId),
        body: JSON.stringify(msg)
      })
    },
    [
      isDrawing,
      isDrawingAllowed,
      client,
      isFillMode,
      brushSize,
      roomId,
      userID,
      canvasRef,
      lastPositions,
      computeCoords
    ]
  )

  const stopDrawing = useCallback(
    (event) => {
      if (!isDrawingAllowed || !client) return

      setIsDrawing(false)
      delete lastPositions.current[userID]

      const msg = { userID: String(userID), eventType: EVENT_TYPE_STOP }

      client.publish({
        destination: APP_ROOM_STOP_DRAWING(roomId),
        body: JSON.stringify(msg)
      })
    },
    [isDrawingAllowed, client, roomId, userID, setIsDrawing, lastPositions]
  )

  return { startDrawing, draw, stopDrawing }
}
