import { useEffect } from 'react';
import { ASPECT_RATIO, CANVAS_WIDTH_RATIO, CANVAS_HEIGHT_RATIO } from '../../utils/constants';

/*
* A hook that calculates the canvas size according to the window width and height.
* it also listens to window size changes and update the canvas dimensions accordingly.
*/
export const useCanvasResize = (canvasRef) => {
  useEffect(() => {
    const resizeCanvas = () => {
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

      canvas.width = Math.min(width, maxWidth);
      canvas.height = Math.min(height, maxHeight);
      console.log(canvas.width)
      console.log(canvas.height)
      const windowWidth = window.innerWidth;
      const windowHeight = window.innerHeight;
      if(windowWidth < 1300 && windowWidth > 1000)
      {
        canvas.width = 350;
      }
      if(windowHeight < 730 && windowHeight > 700)
      {
        canvas.height = 560;
      }
      console.log(windowWidth)
      console.log(windowHeight)
      ctx.lineWidth = 2;
      ctx.lineCap = 'round';
    };

    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    return () => window.removeEventListener('resize', resizeCanvas);
  }, [canvasRef]);

};
