// src/components/Lobby/Lobby.jsx
import React, { useState, useEffect } from "react";
import { useDispatch } from "react-redux";
import { motion } from "framer-motion";
import { Plus, Trophy, Info } from "lucide-react";
import ThemeToggle from "../common/ThemeToggle";
import LeaderboardOverlay from "../Leaderboard/LeaderboardOverlay";
import RoomTable from "./RoomTable";
import CreditsOverlay from "./CreditsOverlay";
import { setRoom } from "../../store/roomSlice";
import {
  MAX_ROOM_NAME_LENGTH,
  LOBBY_TITLE,
  NEW_ROOM_PLACEHOLDER,
  CREATE_ROOM_BUTTON_TEXT,
  ROOM_NAME_EMPTY_ERROR,
  SERVER_CONNECTION_ERROR,
  ROOM_NAME_MAX_ERROR,
} from "../../utils/constants";
import {
  TOPIC_ROOMS,
  APP_GET_ROOMS,
  APP_CREATE_ROOM,
  APP_JOIN_ROOM,
  USER_TOPIC_ROOM_CREATED,
} from "../../utils/subscriptionConstants";
import AppHeader from "../common/AppHeader";

function Lobby({ client, connected }) {
  const [newRoomName, setNewRoomName] = useState("");
  const [error, setError] = useState("");
  const [rooms, setRooms] = useState([]);
  const [showLeaderboard, setShowLeaderboard] = useState(false);
  const [showCredits, setShowCredits] = useState(false);
  const dispatch = useDispatch();

  useEffect(() => {
    if (!client || !connected) return;
    const subscription = client.subscribe(TOPIC_ROOMS, (message) => {
      const data = JSON.parse(message.body);
      const sorted = data.sort((a, b) => {
        if (b.numberOfParticipants !== a.numberOfParticipants) {
          return b.numberOfParticipants - a.numberOfParticipants;
        }
        return a.roomName.localeCompare(b.roomName);
      });
      setRooms(sorted);
    });

    client.publish({
      destination: APP_GET_ROOMS,
      body: "",
    });

    return () => subscription.unsubscribe();
  }, [client, connected]);

  useEffect(() => {
    if (!client || !connected) return;
    const sub = client.subscribe(USER_TOPIC_ROOM_CREATED, (message) => {
      const room = JSON.parse(message.body);
      dispatch(setRoom(room));
    });
    return () => sub.unsubscribe();
  }, [client, connected, dispatch]);

  function handleJoinRoom(room) {
    if (client && connected) {
      client.publish({
        destination: APP_JOIN_ROOM,
        body: room.roomId,
      });
      dispatch(setRoom(room));
    }
  }

  function handleCreateRoom(e) {
    e.preventDefault();

    if (newRoomName === "") {
      setError(ROOM_NAME_EMPTY_ERROR);
      return;
    }

    if (newRoomName.length > MAX_ROOM_NAME_LENGTH) {
      setError(ROOM_NAME_MAX_ERROR);
      return;
    }

    if (client && connected) {
      client.publish({
        destination: APP_CREATE_ROOM,
        body: newRoomName,
      });

      client.publish({
        destination: APP_GET_ROOMS,
        body: "",
      });

      setNewRoomName("");
      setError("");
    } else {
      setError(SERVER_CONNECTION_ERROR);
    }
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 px-4 pt-10 pb-6"
    >
      <div className="max-w-4xl mx-auto">
        <AppHeader
          title={LOBBY_TITLE}
          right={<ThemeToggle />}
          className="mb-10cfc"
        />

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="mb-10"
        >
          <RoomTable rooms={rooms} onJoinRoom={handleJoinRoom} />
        </motion.div>

        <motion.form
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          onSubmit={handleCreateRoom}
          className="card p-6 mb-10"
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

      {showLeaderboard && (
        <LeaderboardOverlay onClose={() => setShowLeaderboard(false)} />
      )}

      {showCredits && <CreditsOverlay onClose={() => setShowCredits(false)} />}
    </motion.div>
  );
}

export default Lobby;
