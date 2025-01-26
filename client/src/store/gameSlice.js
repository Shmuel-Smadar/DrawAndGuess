import { createSlice } from '@reduxjs/toolkit'

const gameSlice = createSlice({
  name: 'game',
  initialState: {
    isDrawer: false,
    showWordSelection: false,
    wordOptions: [],
    isDrawingAllowed: false
  },
  reducers: {
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

export const { setIsDrawer, setShowWordSelection, setWordOptions } = gameSlice.actions
export default gameSlice.reducer