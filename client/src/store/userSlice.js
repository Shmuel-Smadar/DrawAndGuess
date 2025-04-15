import { createSlice } from '@reduxjs/toolkit'

/*
 * Manages user-related state: nickname, session ID, nicknameError.
 */
const userSlice = createSlice({
  name: 'user',
  initialState: {
    username: '',
    nicknameError: '',
    sessionId: ''
  },
  reducers: {
    setUsername: (state, action) => {
      state.username = action.payload
    },
    setNicknameError: (state, action) => {
      state.nicknameError = action.payload
    },
    setSessionId: (state, action) => {
      state.sessionId = action.payload
    }
  }
})

export const { setUsername, setNicknameError, setSessionId } = userSlice.actions
export default userSlice.reducer