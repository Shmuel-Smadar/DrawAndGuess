import { createSlice } from '@reduxjs/toolkit'


/*
 * Manages the currently joined room state.
 */
const roomSlice = createSlice({
  name: 'room',
  initialState: {
    room: null
  },
  reducers: {
    setRoom: (state, action) => {
      state.room = action.payload
    }
  }
})

export const { setRoom } = roomSlice.actions
export default roomSlice.reducer
