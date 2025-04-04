export const VIRTUAL_WIDTH = 10
export const VIRTUAL_HEIGHT = 16
export const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT

export const TOPIC_ROOMS = '/topic/rooms'
export const APP_GET_ROOMS = '/app/getRooms'
export const APP_CREATE_ROOM = '/app/createRoom'
export const APP_JOIN_ROOM = '/app/joinRoom'
export const USER_TOPIC_ROOM_CREATED = '/user/topic/roomCreated'
export const USER_TOPIC_NICKNAME = '/user/topic/nickname'
export const USER_TOPIC_WORD_OPTIONS = '/user/topic/wordOptions'
export const topicRoomChat = (roomId) => `/topic/room/${roomId}/chat`
export const topicRoomDrawing = (roomId) => `/topic/room/${roomId}/drawing`
export const topicRoomClearCanvas = (roomId) => `/topic/room/${roomId}/clearCanvas`
export const topicRoomParticipants = (roomId) => `/topic/room/${roomId}/participants`
export const topicRoomWordHint = (roomId) => `/topic/room/${roomId}/wordHint`
export const appGetCurrentHint = (roomId) => `/app/room/${roomId}/getCurrentHint`
