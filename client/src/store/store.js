import { configureStore } from '@reduxjs/toolkit'
import userReducer from './userSlice'
import roomReducer from './roomSlice'
import gameReducer from './gameSlice'
import drawReducer from './drawSlice'

export const store = configureStore({
  reducer: {
    user: userReducer,
    room: roomReducer,
    draw: drawReducer,
    game: gameReducer
  }
})

export default store
