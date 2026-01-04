import React, { useState, useEffect, useRef, useCallback } from 'react'
import { useSelector } from 'react-redux'
import { motion, AnimatePresence } from 'framer-motion'
import { MessageCircle, Send, ChevronDown, User } from 'lucide-react'

import {
  SYSTEM_MESSAGE_COLORS,
  CHAT_TITLE,
  SCROLL_BUTTON_LABEL,
  NEW_MESSAGES_LABEL,
  CHAT_PLACEHOLDER,
  MAX_CHAT_MESSAGE_LENGTH
} from '../../utils/constants'
import { TOPIC_ROOM_CHAT, APP_ROOM_CHAT } from '../../utils/subscriptionConstants'
import WinnerPrompt from '../Prompt/WinnerPrompt'


/*
 * Chat component that shows messages from all participants (and system messages from the server),
 * and allows the user to send text if they are not the drawer.
 */

const Chat = ({ client, height }) => {
  const [messages, setMessages] = useState([])
  const [newMessage, setNewMessage] = useState('')
  const [isAtBottom, setIsAtBottom] = useState(true)
  const [showScrollButton, setShowScrollButton] = useState(false)
  const [unreadCount, setUnreadCount] = useState(0)
  const [showWinnerPrompt, setShowWinnerPrompt] = useState(false)
  const chatWindowRef = useRef(null)
  const sessionId = useSelector(state => state.user.sessionId)
  const roomId = useSelector(state => state.room.room?.roomId)
  const username = useSelector(state => state.user.username)
  const isDrawer = useSelector(state => state.game.isDrawer)

  //Assigns special color for various type of system messages
  function getSystemMessageColor(messageType) {
    return SYSTEM_MESSAGE_COLORS[messageType] || 'gray'
  }

  /* Subscribes to chat messages, also parse the message to see if it notifies that the
  * current user won the game. if so, displays winner prompt */
  useEffect(() => {
    if (!client || !client.connected || !roomId) return
    const subscription = client.subscribe(TOPIC_ROOM_CHAT(roomId), (message) => {
      const chatMessage = JSON.parse(message.body)
      setMessages((prev) => [...prev, chatMessage])
      if (!isAtBottom) {
        setShowScrollButton(true);
        setUnreadCount((prevCount) => prevCount + 1)
      }
      if (chatMessage.senderSessionId === 'system' && chatMessage.messageType === 'WINNER_ANNOUNCED') {
        if (chatMessage.winnerSessionId === sessionId) {
          setShowWinnerPrompt(true);
        }
      }
    })

    return () => subscription.unsubscribe()
  }, [client, roomId, isAtBottom, sessionId])


  /*
  * Determains scrolling behavior. if the user is at the bottom, a new message will cause auto scrolling by default.
  * if the user is not a the bottom, there won't be auto scrolling,
  * but a small notification about new messages will be displayed
  */
  useEffect(() => {
    if (isAtBottom && chatWindowRef.current) {
      chatWindowRef.current.scrollTop = chatWindowRef.current.scrollHeight
    }
  }, [messages, isAtBottom])

  useEffect(() => {
    const chatWindow = chatWindowRef.current
    if (!chatWindow) return
    const handleScroll = () => {
      const { scrollTop, scrollHeight, clientHeight } = chatWindow
      const atBottom = scrollHeight - scrollTop - clientHeight < 100
      setIsAtBottom(atBottom)
      if (atBottom) {
        setShowScrollButton(false)
        setUnreadCount(0)
      }
    }
    chatWindow.addEventListener('scroll', handleScroll)
    return () => {
      chatWindow.removeEventListener('scroll', handleScroll)
    }
  }, [])

  const scrollToBottom = useCallback(() => {
    if (!chatWindowRef.current) return
    chatWindowRef.current.scrollTo({
      top: chatWindowRef.current.scrollHeight,
      behavior: 'smooth'
    })
    setShowScrollButton(false)
    setIsAtBottom(true)
    setUnreadCount(0)
  }, [])

  const handleSendMessage = () => {
    if (!client || !roomId || newMessage.trim() === '' || !sessionId) return
    if (newMessage.length > MAX_CHAT_MESSAGE_LENGTH) return

    const messageData = {
      text: newMessage,
      senderSessionId: sessionId,
      senderUsername: username,
      type: 'user'
    }

    client.publish({
      destination: APP_ROOM_CHAT(roomId),
      body: JSON.stringify(messageData)
    })
    setNewMessage('')
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="flex flex-col h-full bg-white dark:bg-gray-800"
      style={{ height: `${height}px` }}
    >
      {/* Chat Header */}
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="flex items-center gap-2 px-4 py-3 bg-gradient-to-r from-primary-500 to-primary-600 text-white border-b border-primary-700"
      >
        <MessageCircle className="w-5 h-5" />
        <h3 className="font-semibold text-lg">{CHAT_TITLE}</h3>
      </motion.div>

      {/* Chat Messages */}
      <div className="flex-1 overflow-y-auto p-4 space-y-3 bg-gray-50 dark:bg-gray-900" ref={chatWindowRef}>
        <AnimatePresence>
          {messages.map((message, index) => (
            <motion.div
              key={`${message.senderSessionId}-${index}`}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.3 }}
              className={`max-w-[85%] ${
                message.senderSessionId === 'system'
                  ? 'mx-auto text-center'
                  : message.senderSessionId === sessionId
                    ? 'ml-auto'
                    : 'mr-auto'
              }`}
            >
              {message.senderSessionId === 'system' ? (
                <motion.div
                  className="inline-block px-3 py-2 rounded-full text-sm font-medium"
                  style={{
                    backgroundColor: `${getSystemMessageColor(message.messageType)}20`,
                    color: getSystemMessageColor(message.messageType),
                    border: `1px solid ${getSystemMessageColor(message.messageType)}40`
                  }}
                  whileHover={{ scale: 1.02 }}
                >
                  {message.text}
                </motion.div>
              ) : (
                <motion.div
                  className={`px-4 py-2 rounded-2xl shadow-sm ${
                    message.senderSessionId === sessionId
                      ? 'bg-primary-500 text-white ml-auto'
                      : 'bg-white dark:bg-gray-700 text-gray-900 dark:text-gray-100'
                  }`}
                  whileHover={{ scale: 1.01 }}
                >
                  <div className="flex items-center gap-2 mb-1">
                    <User className="w-3 h-3" />
                    <span className={`text-xs font-medium ${
                      message.senderSessionId === sessionId
                        ? 'text-primary-100'
                        : 'text-gray-600 dark:text-gray-400'
                    }`}>
                      {message.senderUsername}
                    </span>
                  </div>
                  <p className="text-sm leading-relaxed break-words">{message.text}</p>
                </motion.div>
              )}
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Scroll to Bottom Button */}
      <AnimatePresence>
        {showScrollButton && (
          <motion.button
            initial={{ opacity: 0, scale: 0.8, y: 10 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.8, y: 10 }}
            onClick={scrollToBottom}
            className="absolute bottom-20 right-4 bg-primary-500 hover:bg-primary-600 text-white px-3 py-2 rounded-full shadow-lg flex items-center gap-2 text-sm font-medium transition-colors duration-200"
            aria-label={SCROLL_BUTTON_LABEL}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            <ChevronDown className="w-4 h-4" />
            {unreadCount > 0 ? `${unreadCount} ${NEW_MESSAGES_LABEL}` : NEW_MESSAGES_LABEL}
          </motion.button>
        )}
      </AnimatePresence>

      {/* Chat Input */}
      <motion.div
        initial={{ opacity: 0, y: 10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="p-4 border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800"
      >
        <div className="flex gap-2">
          <input
            type="text"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            placeholder={isDrawer ? "Drawing... you can't chat now!" : CHAT_PLACEHOLDER}
            onKeyDown={(e) => e.key === 'Enter' && !isDrawer && handleSendMessage()}
            disabled={isDrawer}
            aria-label="Type your message"
            maxLength={MAX_CHAT_MESSAGE_LENGTH}
            className="input-field flex-1 text-sm disabled:opacity-50 disabled:cursor-not-allowed"
          />
          <motion.button
            onClick={handleSendMessage}
            disabled={isDrawer || !newMessage.trim()}
            className="btn-primary flex items-center gap-2 px-4 disabled:opacity-50 disabled:cursor-not-allowed disabled:hover:scale-100"
            whileHover={{ scale: newMessage.trim() && !isDrawer ? 1.02 : 1 }}
            whileTap={{ scale: newMessage.trim() && !isDrawer ? 0.98 : 1 }}
          >
            <Send className="w-4 h-4" />
          </motion.button>
        </div>
      </motion.div>

      {/* Winner Prompt */}
      <AnimatePresence>
        {showWinnerPrompt && (
          <WinnerPrompt
            username={username}
            client={client}
            connected={client && client.connected}
            onClose={() => setShowWinnerPrompt(false)}
          />
        )}
      </AnimatePresence>
    </motion.div>
  )
}

export default Chat
