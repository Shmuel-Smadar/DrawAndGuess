import { useEffect } from "react";
import {
  ASPECT_RATIO,
  CANVAS_WIDTH_RATIO,
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
      const isMobileLayout = window.innerWidth < 1024;
      const maxWidth = isMobileLayout
        ? window.innerWidth - 24
        : Math.min(window.innerWidth * 0.5, window.innerWidth * CANVAS_WIDTH_RATIO);
      const maxHeight = isMobileLayout
        ? window.innerHeight * 0.72
        : Math.max(420, Math.min(window.innerHeight * 0.72, window.innerHeight - 235));

      let width, height;
      if (maxWidth / maxHeight > ASPECT_RATIO) {
        height = maxHeight;
        width = height * ASPECT_RATIO;
      } else {
        width = maxWidth;
        height = width / ASPECT_RATIO;
      }

      width = Math.floor(width);
      height = Math.floor(height);

      const hasDrawableSize = canvas.width > 0 && canvas.height > 0;
      const imageData = hasDrawableSize
        ? ctx.getImageData(0, 0, canvas.width, canvas.height)
        : null;

      canvas.style.width = `${width}px`;
      canvas.style.height = `${height}px`;
      canvas.width = width;
      canvas.height = height;

      if (imageData) {
        const scratch = document.createElement("canvas");
        scratch.width = imageData.width;
        scratch.height = imageData.height;
        scratch.getContext("2d").putImageData(imageData, 0, 0);

        ctx.drawImage(scratch, 0, 0, width, height);
      }

      ctx.lineWidth = 2;
      ctx.lineCap = "round";
    };

    window.addEventListener("resize", resizeCanvas);
    resizeCanvas();

    return () => window.removeEventListener("resize", resizeCanvas);
  }, [canvasRef]);
};
