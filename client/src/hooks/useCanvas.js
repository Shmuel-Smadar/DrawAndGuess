import { useRef, useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useCanvasResize } from './useCanvasResize';
import { useCanvasSubscriptions } from './useCanvasSubscriptions';
import { useDrawingHandlers } from './useDrawingHandlers';

const useCanvas = ({ client, userID, roomId }) => {
  const color = useSelector((state) => state.draw.color);
  const brushSize = useSelector((state) => state.draw.brushSize);
  const isFillMode = useSelector((state) => state.draw.isFillMode);
  const isDrawingAllowed = useSelector((state) => state.game.isDrawingAllowed);
  const dispatch = useDispatch();

  const canvasRef = useRef(null);
  const lastPositions = useRef({});
  const [isDrawing, setIsDrawing] = useState(false); // <-- isDrawing state

  const resizeCanvas = useCanvasResize(canvasRef);

  useEffect(() => {
    resizeCanvas();
  }, [resizeCanvas]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (canvas) {
      const ctx = canvas.getContext('2d');
      ctx.strokeStyle = color;
    }
  }, [color]);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (canvas) {
      const ctx = canvas.getContext('2d');
      ctx.lineWidth = brushSize;
    }
  }, [brushSize]);

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
    isDrawing,
  });

  return {
    canvasRef,
    handleMouseDown: startDrawing,
    handleMouseMove: draw,
    handleMouseUp: stopDrawing,
    handleMouseLeave: stopDrawing,
    handleTouchStart: startDrawing,
    handleTouchMove: draw,
    handleTouchEnd: stopDrawing,
  };
};

export default useCanvas;