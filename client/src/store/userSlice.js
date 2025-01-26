import { createSlice } from '@reduxjs/toolkit'

const userSlice = createSlice({
  name: 'user',
  initialState: {
    username: '',
    nicknameError: ''
  },
  reducers: {
    setUsername: (state, action) => {
      state.username = action.payload
    },
    setNicknameError: (state, action) => {
      state.nicknameError = action.payload
    }
  }
})

export const { setUsername, setNicknameError } = userSlice.actions
export default userSlice.reducer
