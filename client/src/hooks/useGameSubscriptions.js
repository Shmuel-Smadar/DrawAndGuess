import { useEffect } from 'react'
import { useDispatch } from 'react-redux'
import { setWordOptions, setShowWordSelection } from '../store/gameSlice'
import { USER_TOPIC_WORD_OPTIONS, TOPIC_ROOM_CHAT } from '../utils/subscriptionConstants'

const useGameSubscriptions = (client, connected, room, isDrawer, requestWordOptions) => {
  const dispatch = useDispatch()

  useEffect(() => {
    if (!client || !connected) return
    const sub = client.subscribe(USER_TOPIC_WORD_OPTIONS, (msg) => {
      const data = JSON.parse(msg.body)
      dispatch(setWordOptions([data.word1, data.word2, data.word3]))
      dispatch(setShowWordSelection(true))
    })
    return () => sub.unsubscribe()
  }, [client, connected, dispatch])

  useEffect(() => {
    if (!client || !connected || !room) return
    const chatSub = client.subscribe(TOPIC_ROOM_CHAT(room.roomId), (msg) => {
      const message = JSON.parse(msg.body)
      if (message.type === 'system' && message.messageType === 'NEW_GAME_STARTED' && isDrawer) {
        requestWordOptions()
      }
    })
    return () => chatSub.unsubscribe()
  }, [client, connected, room, isDrawer, requestWordOptions])
}

export default useGameSubscriptions
