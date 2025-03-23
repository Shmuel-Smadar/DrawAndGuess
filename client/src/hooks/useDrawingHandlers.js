import { useCallback } from 'react';
import { setIsFillMode } from '../store/drawSlice';
import { getEventCoordinates } from '../utils/helpers';
import { VIRTUAL_WIDTH, VIRTUAL_HEIGHT } from '../utils/constants';

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
  isDrawing,
}) => {
  const startDrawing = useCallback(
    (event) => {
      event.preventDefault();
      if (!isDrawingAllowed || !client) return;

      const ctx = canvasRef.current.getContext('2d');

      if (isFillMode) {
        const { offsetX, offsetY } = getEventCoordinates(event, canvasRef);
        const { width, height } = canvasRef.current;
        const normX = (offsetX / width) * VIRTUAL_WIDTH;
        const normY = (offsetY / height) * VIRTUAL_HEIGHT;

        const msg = {
          normX,
          normY,
          color,
          userID: String(userID),
          eventType: 'FILL',
        };

        client.publish({
          destination: `/app/room/${roomId}/fill`,
          body: JSON.stringify(msg),
        });

        dispatch(setIsFillMode(false));
        return;
      }

      const { offsetX, offsetY } = getEventCoordinates(event, canvasRef);
      const { width, height } = canvasRef.current;
      const normX = (offsetX / width) * VIRTUAL_WIDTH;
      const normY = (offsetY / height) * VIRTUAL_HEIGHT;

      ctx.beginPath();
      ctx.moveTo(offsetX, offsetY);
      ctx.strokeStyle = color;

      setIsDrawing(true);
      lastPositions.current[userID] = { x: offsetX, y: offsetY };

      const message = {
        normX,
        normY,
        color,
        brushSize,
        userID: String(userID),
        eventType: 'START',
      };

      client.publish({
        destination: `/app/room/${roomId}/startDrawing`,
        body: JSON.stringify(message),
      });
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
    ]
  );

  const draw = useCallback(
    (event) => {
      if (!isDrawing || !isDrawingAllowed || !client || isFillMode) return;

      const { offsetX, offsetY } = getEventCoordinates(event, canvasRef);
      const { width, height } = canvasRef.current;
      const normX = (offsetX / width) * VIRTUAL_WIDTH;
      const normY = (offsetY / height) * VIRTUAL_HEIGHT;

      const ctx = canvasRef.current.getContext('2d');
      ctx.lineTo(offsetX, offsetY);
      ctx.stroke();

      lastPositions.current[userID] = { x: offsetX, y: offsetY };

      const msg = {
        normX,
        normY,
        brushSize,
        userID: String(userID),
        eventType: 'DRAW',
      };

      client.publish({
        destination: `/app/room/${roomId}/draw`,
        body: JSON.stringify(msg),
      });
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
    ]
  );

  const stopDrawing = useCallback(
    (event) => {
      event.preventDefault();
      if (!isDrawingAllowed || !client) return;

      setIsDrawing(false);
      delete lastPositions.current[userID];

      const msg = { userID: String(userID), eventType: 'STOP' };

      client.publish({
        destination: `/app/room/${roomId}/stopDrawing`,
        body: JSON.stringify(msg),
      });
    },
    [isDrawingAllowed, client, roomId, userID, setIsDrawing, lastPositions]
  );

  return { startDrawing, draw, stopDrawing };
};