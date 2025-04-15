/**
 * Helper functions for coordinate handling and flood fill
 *  (filling a closed area in the canvas with some color)
 */

export const getEventCoordinates = (event, canvasRef) => {
  if (event.nativeEvent instanceof MouseEvent) {
    return { offsetX: event.nativeEvent.offsetX, offsetY: event.nativeEvent.offsetY }
  } else if (event.nativeEvent instanceof TouchEvent) {
    const rect = canvasRef.current.getBoundingClientRect()
    return {
      offsetX: event.nativeEvent.touches[0].clientX - rect.left,
      offsetY: event.nativeEvent.touches[0].clientY - rect.top,
    };
  }
};
export function floodFill(ctx, x, y, fillHex) {
  const { width, height } = ctx.canvas
  const imgData = ctx.getImageData(0, 0, width, height)
  const data = imgData.data
  function getPixel(xx, yy) {
    const i = (yy * width + xx) * 4
    return [data[i], data[i + 1], data[i + 2], data[i + 3]]
  }
  const [targetR, targetG, targetB, targetA] = getPixel(x, y)
  const fillR = parseInt(fillHex.slice(1, 3), 16)
  const fillG = parseInt(fillHex.slice(3, 5), 16)
  const fillB = parseInt(fillHex.slice(5, 7), 16)

  if (targetR === fillR && targetG === fillG && targetB === fillB && targetA === 255) {
    return
  }

  const stack = [[x, y]]
  while (stack.length) {
    const [cx, cy] = stack.pop()
    const [r, g, b, a] = getPixel(cx, cy)
    if (r === targetR && g === targetG && b === targetB && a === targetA) {
      const i = (cy * width + cx) * 4
      data[i] = fillR
      data[i + 1] = fillG
      data[i + 2] = fillB
      data[i + 3] = 255
      if (cx > 0) stack.push([cx - 1, cy])
      if (cx < width - 1) stack.push([cx + 1, cy])
      if (cy > 0) stack.push([cx, cy - 1])
      if (cy < height - 1) stack.push([cx, cy + 1])
    }
  }
  ctx.putImageData(imgData, 0, 0)
}