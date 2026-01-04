import React, {useState, useEffect, useRef } from 'react'
import { useSelector } from 'react-redux'
import { motion } from 'framer-motion'
import { Users, Crown, Trophy } from 'lucide-react'
import { TOPIC_ROOM_PARTICIPANTS, APP_GET_PARTICIPANTS } from '../../utils/subscriptionConstants'
import { PARTICIPANTS_TITLE, DRAWING_INDICATOR_TEXT } from '../../utils/constants'


/*
 * Shows a list of participants in the current room, 
 * with scores and an indicator of the current drawer.
 */
const ParticipantsList = ({client, height, onDrawerChange }) => {
  const [userList, setUserList] = useState([])
  const participantsWindowRef = useRef(null)
  const roomId = useSelector(state => state.room.room?.roomId)
  const username = useSelector(state => state.user.username)

  useEffect(() => {
    if (!client || !roomId) return
    const destination = TOPIC_ROOM_PARTICIPANTS(roomId)
    const subscription = client.subscribe(destination, (message) => {
      const participants = JSON.parse(message.body)
      setUserList(participants)
      const me = participants.find((p) => p.username === username)
      onDrawerChange(me && me.isDrawer)
    })
    return () => {
      subscription.unsubscribe()
    }
  }, [client, roomId, username, onDrawerChange])

  useEffect(() => {
    if (!client || !roomId) return
    client.publish({
      destination: APP_GET_PARTICIPANTS(roomId),
      body: ''
    })
  }, [client, roomId])

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="flex flex-col h-full bg-white dark:bg-gray-800"
      style={{ height: `${height}px` }}
    >
      {/* Participants Header */}
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="flex items-center gap-2 px-4 py-3 bg-gradient-to-r from-secondary-500 to-secondary-600 text-white border-b border-secondary-700"
      >
        <Users className="w-5 h-5" />
        <h3 className="font-semibold text-lg">{PARTICIPANTS_TITLE}</h3>
        <span className="ml-auto bg-white/20 px-2 py-1 rounded-full text-xs font-medium">
          {userList.length}/10
        </span>
      </motion.div>

      {/* Participants List */}
      <div className="flex-1 overflow-y-auto p-4 space-y-2 bg-gray-50 dark:bg-gray-900" ref={participantsWindowRef}>
        {userList.map((user, index) => (
          <motion.div
            key={user.sessionId}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.1 }}
            className={`flex items-center justify-between p-3 rounded-lg transition-all duration-200 ${
              user.username === username
                ? 'bg-primary-100 dark:bg-primary-900/30 border border-primary-300 dark:border-primary-700'
                : 'bg-white dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600'
            }`}
          >
            <div className="flex items-center gap-3">
              <div className={`w-8 h-8 rounded-full flex items-center justify-center ${
                user.username === username
                  ? 'bg-primary-500 text-white'
                  : 'bg-gray-300 dark:bg-gray-600 text-gray-700 dark:text-gray-300'
              }`}>
                <span className="text-sm font-bold">
                  {user.username.charAt(0).toUpperCase()}
                </span>
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <span className={`font-medium ${
                    user.username === username
                      ? 'text-primary-700 dark:text-primary-300'
                      : 'text-gray-900 dark:text-gray-100'
                  }`}>
                    {user.username}
                  </span>
                  {user.username === username && (
                    <span className="text-xs bg-primary-500 text-white px-2 py-0.5 rounded-full">
                      You
                    </span>
                  )}
                </div>
                <div className="flex items-center gap-1 text-sm text-gray-600 dark:text-gray-400">
                  <Trophy className="w-3 h-3" />
                  <span>{user.score} points</span>
                </div>
              </div>
            </div>

            {user.isDrawer && (
              <motion.div
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
                transition={{ type: "spring", stiffness: 500, damping: 15 }}
                className="flex items-center gap-1 bg-warning-500 text-white px-2 py-1 rounded-full text-xs font-medium"
              >
                <Crown className="w-3 h-3" />
                <span>{DRAWING_INDICATOR_TEXT}</span>
              </motion.div>
            )}
          </motion.div>
        ))}
      </div>
    </motion.div>
  )
}

export default ParticipantsList
