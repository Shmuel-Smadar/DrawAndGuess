import { createSlice } from '@reduxjs/toolkit'

/*
 * Manages game-related state: color, brush size, fill mode, isDrawer,
 * and showing word selection to the drawer
 */

const initialState = {
  color: '#000000',
  brushSize: 2,
  isFillMode: false,
  isDrawer: false,
  showWordSelection: false,
  wordOptions: [],
  isDrawingAllowed: false
}

const gameSlice = createSlice({
  name: 'game',
  initialState,
  reducers: {
    setColor: (state, action) => {
      state.color = action.payload
    },
    setBrushSize: (state, action) => {
      state.brushSize = action.payload
    },
    setIsFillMode: (state, action) => {
      state.isFillMode = action.payload
    },
    setIsDrawer: (state, action) => {
      state.isDrawer = action.payload
      state.isDrawingAllowed = action.payload
    },
    setShowWordSelection: (state, action) => {
      state.showWordSelection = action.payload
    },
    setWordOptions: (state, action) => {
      state.wordOptions = action.payload
    }
  }
})

export const {
  setColor,
  setBrushSize,
  setIsFillMode,
  setIsDrawer,
  setShowWordSelection,
  setWordOptions
} = gameSlice.actions

export default gameSlice.reducer
