import React, { useState, useEffect } from 'react'
import { useSelector } from 'react-redux'
import { motion } from 'framer-motion'
import { Eye } from 'lucide-react'
import { TOPIC_ROOM_WORD_HINT, APP_GET_CURRENT_HINT } from '../../utils/subscriptionConstants'

// Shows a hint of the current word to the guessers.
const WordHint = ({ client }) => {
  const [currentHint, setCurrentHint] = useState('')
  const roomId = useSelector(state => state.room.room?.roomId)
  const isDrawer = useSelector(state => state.game.isDrawer)

  useEffect(() => {
    if (!client || !client.connected || isDrawer) return
    const subscription = client.subscribe(TOPIC_ROOM_WORD_HINT(roomId), (message) => {
      const hint = message.body
      setCurrentHint(hint)
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, roomId, isDrawer])

  useEffect(() => {
    if (!client || !roomId) return
    client.publish({
      destination: APP_GET_CURRENT_HINT(roomId),
      body: ''
    })
  }, [client, roomId])

  if (isDrawer) return null

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      transition={{ duration: 0.3 }}
      className="card p-4 max-w-sm mx-auto"
    >
      <div className="flex items-center gap-2 mb-3">
        <Eye className="w-5 h-5 text-secondary-500" />
        <h3 className="font-semibold text-gray-800 dark:text-gray-200">Word Hint</h3>
      </div>

      <motion.div
        className="flex flex-wrap gap-1 justify-center p-3 bg-gray-50 dark:bg-gray-800 rounded-lg"
        key={currentHint} // Re-animate when hint changes
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        {currentHint.split('').map((char, index) => (
          <motion.span
            key={`${char}-${index}`}
            className="inline-flex items-center justify-center w-8 h-8 bg-white dark:bg-gray-700 border border-gray-300 dark:border-gray-600 rounded font-bold text-lg text-gray-800 dark:text-gray-200"
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: index * 0.05 }}
          >
            {(char === ' ' || char === '_') ? '\u00A0' : char}
          </motion.span>
        ))}
      </motion.div>

      {currentHint && (
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-center text-sm text-gray-600 dark:text-gray-400 mt-2"
        >
          Guess the word being drawn!
        </motion.p>
      )}
    </motion.div>
  )
}

export default WordHint
