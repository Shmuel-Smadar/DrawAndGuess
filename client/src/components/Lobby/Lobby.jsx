import React, { useState, useEffect } from 'react'
import { useDispatch } from 'react-redux'
import { motion } from 'framer-motion'
import { Plus, Trophy, Info } from 'lucide-react'
import ThemeToggle from '../common/ThemeToggle'
import LeaderboardOverlay from '../Leaderboard/LeaderboardOverlay'
import RoomTable from './RoomTable'
import CreditsOverlay from './CreditsOverlay'
import { setRoom } from '../../store/roomSlice'
import {
  MAX_ROOM_NAME_LENGTH,
  LOBBY_TITLE,
  NEW_ROOM_PLACEHOLDER,
  CREATE_ROOM_BUTTON_TEXT,
  ROOM_NAME_EMPTY_ERROR,
  SERVER_CONNECTION_ERROR,
  ROOM_NAME_MAX_ERROR
} from '../../utils/constants'
import {
  TOPIC_ROOMS,
  APP_GET_ROOMS,
  APP_CREATE_ROOM,
  APP_JOIN_ROOM,
  USER_TOPIC_ROOM_CREATED
} from '../../utils/subscriptionConstants'



/*
 * The main lobby page that lists all the active rooms,
 * allows creating a new room, and also provides buttons
 * to open the leaderboard or credits overlays.
 * */
function Lobby({client, connected}) {
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState('')
  const [rooms, setRooms] = useState([])
  const [showLeaderboard, setShowLeaderboard] = useState(false)
  const [showCredits, setShowCredits] = useState(false)
  const dispatch = useDispatch()

  // A hook that gets a list of rooms and sort them in the room table
  useEffect(() => {
    if (!client || !connected) return
    const subscription = client.subscribe(TOPIC_ROOMS, (message) => {
      const data = JSON.parse(message.body)
      const sorted = data.sort((a, b) => {
        if (b.numberOfParticipants !== a.numberOfParticipants) {
          return b.numberOfParticipants - a.numberOfParticipants
        }
        return a.roomName.localeCompare(b.roomName)
      })
      setRooms(sorted)
    })

    client.publish({
      destination: APP_GET_ROOMS,
      body: ''
    })

    return () => subscription.unsubscribe()
  }, [client, connected])

  // A hook that gets called when the current user created a room and the server notified about it
  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe(USER_TOPIC_ROOM_CREATED, (message) => {
      const room = JSON.parse(message.body)
      dispatch(setRoom(room))
    })
    return () => sub.unsubscribe()
  }, [client, connected, dispatch])

  function handleJoinRoom(room) {
    if (client && connected) {
      client.publish({
        destination: APP_JOIN_ROOM,
        body: room.roomId,
      });
      dispatch(setRoom(room));
    }
  }

  // A function that handles room creation
  function handleCreateRoom(e) {
    e.preventDefault()
    if (newRoomName === '') {
      setError(ROOM_NAME_EMPTY_ERROR)
      return
    }
    if (newRoomName.length > MAX_ROOM_NAME_LENGTH) {
      setError(ROOM_NAME_MAX_ERROR)
      return
    }
    if (client && connected) {
      client.publish({
        destination: APP_CREATE_ROOM,
        body: newRoomName,
      })
      client.publish({
        destination: APP_GET_ROOMS,
        body: ''
      })
      setNewRoomName('')
      setError('')
    } else {
      setError(SERVER_CONNECTION_ERROR)
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 p-4"
    >
      <div className="max-w-4xl mx-auto">
        {/* Header */}
        <div className="flex justify-between items-center mb-8">
          <motion.h1
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.2 }}
            className="game-title"
          >
            {LOBBY_TITLE}
          </motion.h1>
          <ThemeToggle />
        </div>

        {/* Room Table */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="mb-8"
        >
          <RoomTable rooms={rooms} onJoinRoom={handleJoinRoom} />
        </motion.div>

        {/* Create Room Form */}
        <motion.form
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          onSubmit={handleCreateRoom}
          className="card p-6 mb-8"
        >
          <div className="flex flex-col sm:flex-row gap-4">
            <input
              type="text"
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              placeholder={NEW_ROOM_PLACEHOLDER}
              maxLength={MAX_ROOM_NAME_LENGTH}
              className="input-field flex-1"
              required
            />
            <motion.button
              type="submit"
              className="btn-primary flex items-center gap-2 whitespace-nowrap"
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <Plus className="w-4 h-4" />
              {CREATE_ROOM_BUTTON_TEXT}
            </motion.button>
          </div>
          {error && (
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              className="mt-4 p-3 bg-red-100 dark:bg-red-900/30 border border-red-300 dark:border-red-700 rounded-lg text-red-700 dark:text-red-400 text-sm"
            >
              {error}
            </motion.div>
          )}
        </motion.form>

        {/* Action Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
          className="flex flex-col sm:flex-row gap-4 justify-center"
        >
          <motion.button
            onClick={() => setShowLeaderboard(true)}
            className="btn-outline flex items-center gap-2"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <Trophy className="w-4 h-4" />
            Leaderboard
          </motion.button>
          <motion.button
            onClick={() => setShowCredits(true)}
            className="btn-outline flex items-center gap-2"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            <Info className="w-4 h-4" />
            Credits
          </motion.button>
        </motion.div>
      </div>

      {/* Overlays */}
      {showLeaderboard && (
        <LeaderboardOverlay onClose={() => setShowLeaderboard(false)} />
      )}
      {showCredits && (
        <CreditsOverlay onClose={() => setShowCredits(false)} />
      )}
    </motion.div>
  )
}

export default Lobby