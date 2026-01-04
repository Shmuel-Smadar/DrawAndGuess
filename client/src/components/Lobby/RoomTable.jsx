import React from 'react'
import { motion } from 'framer-motion'
import { Users, Gamepad2, ArrowRight } from 'lucide-react'
import EmptyIcon from '../../assets/empty-pockets.png'
import { EMPTY_ROOM_HEADING, EMPTY_ROOM_TEXT, JOIN_ROOM_BUTTON_TEXT } from '../../utils/constants'


// Shows an icon and a message stating that there are no rooms available
function EmptyRoomState() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="text-center py-16 px-8"
    >
      <motion.div
        animate={{ y: [0, -10, 0] }}
        transition={{ duration: 3, repeat: Infinity, ease: "easeInOut" }}
        className="mb-6"
      >
        <img
          src={EmptyIcon}
          alt="No rooms available"
          className="w-24 h-24 mx-auto opacity-60 dark:opacity-40"
        />
      </motion.div>
      <h2 className="text-2xl font-bold text-gray-700 dark:text-gray-300 mb-4">
        {EMPTY_ROOM_HEADING}
      </h2>
      <p className="text-gray-500 dark:text-gray-400 text-lg">
        {EMPTY_ROOM_TEXT}
      </p>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.5 }}
        className="mt-6"
      >
        <Gamepad2 className="w-16 h-16 mx-auto text-primary-400 opacity-50" />
      </motion.div>
    </motion.div>
  )
}

// Displays a scrollable list of rooms and calls EmptyRoomState in case of no rooms
function RoomTable({ rooms, onJoinRoom }) {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="card p-6"
    >
      <div className="flex items-center gap-2 mb-6">
        <Gamepad2 className="w-5 h-5 text-primary-500" />
        <h3 className="text-xl font-semibold text-gray-800 dark:text-gray-200">
          Active Rooms
        </h3>
      </div>

      {rooms.length === 0 ? (
        <EmptyRoomState />
      ) : (
        <div className="space-y-3 max-h-96 overflow-y-auto">
          {rooms.map((room, index) => (
            <motion.div
              key={room.roomId}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="flex items-center justify-between p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors duration-200"
            >
              <div className="flex items-center gap-3 flex-1">
                <div className="w-10 h-10 bg-primary-100 dark:bg-primary-900/30 rounded-lg flex items-center justify-center">
                  <Gamepad2 className="w-5 h-5 text-primary-600 dark:text-primary-400" />
                </div>
                <div className="flex-1">
                  <h4 className="font-medium text-gray-900 dark:text-gray-100">
                    {room.roomName}
                  </h4>
                  <div className="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400">
                    <Users className="w-4 h-4" />
                    <span>{room.numberOfParticipants}/10 players</span>
                  </div>
                </div>
              </div>
              <motion.button
                onClick={() => onJoinRoom(room)}
                className="btn-primary flex items-center gap-2 px-4 py-2"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                {JOIN_ROOM_BUTTON_TEXT}
                <ArrowRight className="w-4 h-4" />
              </motion.button>
            </motion.div>
          ))}
        </div>
      )}
    </motion.div>
  )
}

export default RoomTable
