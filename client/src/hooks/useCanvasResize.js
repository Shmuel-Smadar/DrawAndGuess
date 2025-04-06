import { useCallback } from 'react';
import { ASPECT_RATIO, CANVAS_WIDTH_RATIO, CANVAS_HEIGHT_RATIO } from '../utils/constants';

export const useCanvasResize = (canvasRef) => {
  const resizeCanvas = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const maxWidth = window.innerWidth * CANVAS_WIDTH_RATIO;
    const maxHeight = window.innerHeight * CANVAS_HEIGHT_RATIO;
    let width, height;

    if (maxWidth / maxHeight > ASPECT_RATIO) {
      height = maxHeight;
      width = height * ASPECT_RATIO;
    } else {
      width = maxWidth;
      height = width / ASPECT_RATIO;
    }

    width = Math.min(width, maxWidth);
    height = Math.min(height, maxHeight);

    canvas.width = width;
    canvas.height = height;

    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
  }, [canvasRef]);

  return resizeCanvas;
};