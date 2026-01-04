import { useEffect } from "react";
import {
  ASPECT_RATIO,
  CANVAS_WIDTH_RATIO,
  CANVAS_HEIGHT_RATIO,
} from "../../utils/constants";

/*
 * A hook that calculates the canvas size according to the window width and height.
 * it also listens to window size changes and update the canvas dimensions accordingly.
 * Preserves canvas content during resize by saving and restoring the drawing.
 */
export const useCanvasResize = (canvasRef) => {
  useEffect(() => {
    const resizeCanvas = () => {
      const canvas = canvasRef.current;
      if (!canvas) return;
      const ctx = canvas.getContext("2d");
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

      // Save current canvas content before resizing
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);

      canvas.style.width = `${width}px`;
      canvas.style.height = `${height}px`;
      canvas.width = width;
      canvas.height = height;

      // Restore canvas content, scaling to fit new dimensions
      const scaleX = width / imageData.width;
      const scaleY = height / imageData.height;

      ctx.save();
      ctx.scale(scaleX, scaleY);
      ctx.putImageData(imageData, 0, 0);
      ctx.restore();

      ctx.lineWidth = 2;
      ctx.lineCap = "round";
    };

    window.addEventListener("resize", resizeCanvas);
    resizeCanvas();

    return () => window.removeEventListener("resize", resizeCanvas);
  }, [canvasRef]);
};
