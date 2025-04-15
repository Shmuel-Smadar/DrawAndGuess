import { useRef, useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux'
import { useCanvasResize } from './useCanvasResize';
import { useCanvasSubscriptions } from './useCanvasSubscriptions';
import { useDrawingHandlers } from './useDrawingHandlers';



/*
 * custom hook that covers all the logic related to the canvas:
 * - setting up canvas dimensions (with `useCanvasResize`)
 * - subscribing to drawing events by other users (with `useCanvasSubscriptions`)
 * - handling local user drawing (with `useDrawingHandlers`)
 *
 */
const useCanvas = ({ client}) => {
  const color = useSelector((state) => state.game.color);
  const brushSize = useSelector((state) => state.game.brushSize);
  const isFillMode = useSelector((state) => state.game.isFillMode);
  const isDrawingAllowed = useSelector((state) => state.game.isDrawingAllowed);
  const dispatch = useDispatch();
  const roomId = useSelector(state => state.room.room?.roomId)
  const userID = useSelector(state => state.user.sessionId)

  const canvasRef = useRef(null);
  const lastPositions = useRef({});
  const [isDrawing, setIsDrawing] = useState(false);

  useCanvasResize(canvasRef);
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    ctx.strokeStyle = color;
    ctx.lineWidth = brushSize;
  }, [color, brushSize]);
  useCanvasSubscriptions({ client, roomId, canvasRef, lastPositions });

  const { startDrawing, draw, stopDrawing } = useDrawingHandlers({
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
  });

  return {
    canvasRef,
    handleMouseDown: startDrawing,
    handleMouseMove: draw,
    handleMouseUp: stopDrawing,
    handleMouseLeave: stopDrawing,
    handleTouchStart: startDrawing,
    handleTouchMove: draw,
    handleTouchEnd: stopDrawing
  };
};

export default useCanvas;