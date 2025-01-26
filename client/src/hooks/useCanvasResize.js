import { useCallback } from 'react';
import { VIRTUAL_WIDTH, VIRTUAL_HEIGHT, ASPECT_RATIO } from '../utils/constants';

export const useCanvasResize = (canvasRef) => {
  const resizeCanvas = useCallback(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const maxWidth = window.innerWidth * 0.9;
    const maxHeight = window.innerHeight * 0.8;
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

    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

    canvas.width = width;
    canvas.height = height;

    ctx.putImageData(imageData, 0, 0);
    ctx.lineWidth = 2;
    ctx.lineCap = 'round';
  }, []);

  return resizeCanvas;
};