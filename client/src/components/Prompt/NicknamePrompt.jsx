import React, { useState, useEffect, useRef } from 'react'
import { useDispatch } from 'react-redux'
import { setUsername, setNicknameError, setSessionId } from '../../store/userSlice'
import { 
  MAX_NICKNAME_LENGTH, 
  NICKNAME_PROMPT_TITLE, 
  NICKNAME_PLACEHOLDER, 
  JOIN_BUTTON_TEXT, 
  NICKNAME_INVALID_ERROR 
} from '../../utils/constants'
import { USER_TOPIC_NICKNAME, APP_REGISTER_NICKNAME } from '../../utils/subscriptionConstants'
import './NicknamePrompt.css'

 /*
 * Prompts the user for a nickname on first load.
 * validates the input (with regex) and sends it to the server to ensure
 * the nickname is unique. display an error in case the server returns one.
 */
const NicknamePrompt = ({ client, connected, error }) => {
  const [nicknameInput, setNicknameInput] = useState('')
  const dispatch = useDispatch()
  const currentNickname = useRef('')
  
  // A hook that subscribe to get an answer from the server regarding registration
  useEffect(() => {
    if (client && connected) {
      const subscription = client.subscribe(USER_TOPIC_NICKNAME, (message) => {
        const data = JSON.parse(message.body)
        if (data.success) {
          dispatch(setUsername(currentNickname.current))
          dispatch(setNicknameError(''))
          dispatch(setSessionId(data.sessionId))
        } else {
          dispatch(setNicknameError(data.message))
        }
      })
      return () => subscription.unsubscribe()
    }
  }, [client, connected, dispatch]);

    /* A function which gets called after the user chose a nickname.
    * the nickname then being verified internally. and if valid it is send to the server */
  const handleSubmit = (e) => {
    e.preventDefault();
    if (nicknameInput !== '' && client && connected) {
      if (!nicknameInput.match(/^[A-Za-z0-9\u0590-\u05FF]+$/)) {
        dispatch(setNicknameError(NICKNAME_INVALID_ERROR));
        return;
      }
      currentNickname.current = nicknameInput;
      const registrationRequest = { nickname: nicknameInput }
      client.publish({
        destination: APP_REGISTER_NICKNAME,
        body: JSON.stringify(registrationRequest)
      })
    }
  }

  return (
    <div className="nickname-prompt">
      <form onSubmit={handleSubmit}>
        <h2>{NICKNAME_PROMPT_TITLE}</h2>
        <input
          type="text"
          value={nicknameInput}
          onChange={(e) => setNicknameInput(e.target.value)}
          placeholder={NICKNAME_PLACEHOLDER}
          maxLength={MAX_NICKNAME_LENGTH}
        />
        <button type="submit">{JOIN_BUTTON_TEXT}</button>
        {error && <p className="error-message">{error}</p>}
      </form>
    </div>
  )
}

export default NicknamePrompt