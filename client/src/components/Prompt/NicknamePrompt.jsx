import React, { useState, useEffect, useRef } from 'react'
import { useDispatch } from 'react-redux'
import { motion } from 'framer-motion'
import { User, Sparkles } from 'lucide-react'
import ThemeToggle from '../common/ThemeToggle'
import { setUsername, setNicknameError, setSessionId } from '../../store/userSlice'
import {
  MAX_NICKNAME_LENGTH,
  NICKNAME_PROMPT_TITLE,
  NICKNAME_PLACEHOLDER,
  JOIN_BUTTON_TEXT,
  NICKNAME_INVALID_ERROR
} from '../../utils/constants'
import { USER_TOPIC_NICKNAME, APP_REGISTER_NICKNAME } from '../../utils/subscriptionConstants'

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
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 flex items-center justify-center p-4">
      <div className="absolute top-4 right-4">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, scale: 0.9, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="card p-8 w-full max-w-md"
      >
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="text-center mb-8"
        >
          <motion.div
            animate={{ rotate: [0, 10, -10, 0] }}
            transition={{ duration: 2, repeat: Infinity, repeatDelay: 3 }}
            className="inline-block mb-4"
          >
            <Sparkles className="w-12 h-12 text-primary-500 mx-auto" />
          </motion.div>
          <h2 className="text-2xl font-bold text-gray-800 dark:text-gray-200 mb-2">
            {NICKNAME_PROMPT_TITLE}
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            Choose a nickname to start drawing and guessing!
          </p>
        </motion.div>

        <motion.form
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          onSubmit={handleSubmit}
          className="space-y-6"
        >
          <div className="relative">
            <User className="absolute left-3 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={nicknameInput}
              onChange={(e) => setNicknameInput(e.target.value)}
              placeholder={NICKNAME_PLACEHOLDER}
              maxLength={MAX_NICKNAME_LENGTH}
              className="input-field pl-10 w-full"
            />
          </div>

          <motion.button
            type="submit"
            className="btn-primary w-full flex items-center justify-center gap-2 py-3"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            disabled={!nicknameInput.trim()}
          >
            <User className="w-4 h-4" />
            {JOIN_BUTTON_TEXT}
          </motion.button>

          {error && (
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="p-3 bg-red-100 dark:bg-red-900/30 border border-red-300 dark:border-red-700 rounded-lg text-red-700 dark:text-red-400 text-sm text-center"
            >
              {error}
            </motion.div>
          )}
        </motion.form>
      </motion.div>
    </div>
  )
}

export default NicknamePrompt