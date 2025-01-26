import { createSlice } from '@reduxjs/toolkit'

const drawSlice = createSlice({
  name: 'draw',
  initialState: {
    color: '#000000',
    brushSize: 2,
    isFillMode: false
  },
  reducers: {
    setColor: (state, action) => {
      state.color = action.payload
    },
    setBrushSize: (state, action) => {
      state.brushSize = action.payload
    },
    setIsFillMode: (state, action) => {
      state.isFillMode = action.payload
    }
  }
})

export const { setColor, setBrushSize, setIsFillMode } = drawSlice.actions
export default drawSlice.reducer
