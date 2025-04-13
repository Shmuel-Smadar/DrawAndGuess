import { configureStore } from '@reduxjs/toolkit'
import userReducer from './userSlice'
import roomReducer from './roomSlice'
import gameReducer from './gameSlice'

export const store = configureStore({
  reducer: {
    user: userReducer,
    room: roomReducer,
    game: gameReducer
  }
})

export default store
