import React from 'react';
import { motion } from 'framer-motion';
import Chat from './Chat';
import ParticipantsList from './ParticipantsList';
import { CHAT_HEIGHT_RATIO, PARTICIPANTS_HEIGHT_RATIO } from '../../utils/constants';

/*
 * A right-side layout component that displays:
 * - A chat at the top portion
 * - A participants list at the bottom portion
 */
const RightSidebar = ({client, height, onDrawerChange }) => {
  // Ensure minimum height for usability
  const adjustedHeight = height < 600 ? 560 : height;
  const chatHeight = adjustedHeight * CHAT_HEIGHT_RATIO;
  const participantsHeight = adjustedHeight * PARTICIPANTS_HEIGHT_RATIO;

  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.5 }}
      className="flex flex-col bg-white dark:bg-gray-800 rounded-xl shadow-lg border border-gray-200 dark:border-gray-700 overflow-hidden"
      style={{ height: `${adjustedHeight}px` }}
    >
      <div className="flex-1" style={{ height: `${chatHeight}px` }}>
        <Chat
          client={client}
          height={chatHeight}
        />
      </div>
      <div className="border-t border-gray-200 dark:border-gray-700" style={{ height: `${participantsHeight}px` }}>
        <ParticipantsList
          client={client}
          height={participantsHeight}
          onDrawerChange={onDrawerChange}
        />
      </div>
    </motion.div>
  );
};

export default RightSidebar;