export const APP_GET_ROOMS = '/app/getRooms'
export const APP_CREATE_ROOM = '/app/createRoom'
export const APP_JOIN_ROOM = '/app/joinRoom'
export const APP_REGISTER_NICKNAME = '/app/registerNickname'
export const APP_WINNER_MESSAGE = '/app/winnerMessage'
export const APP_GET_PARTICIPANTS = (roomId) => `/app/room/${roomId}/getParticipants`
export const APP_GET_CURRENT_HINT = (roomId) => `/app/room/${roomId}/getCurrentHint`
export const APP_REQUEST_WORDS = (roomId) => `/app/room/${roomId}/requestWords`
export const APP_CHOOSE_WORD = (roomId) => `/app/room/${roomId}/chooseWord`
export const APP_CLEAR_CANVAS = (roomId) => `/app/room/${roomId}/clearCanvas`
export const APP_ROOM_CHAT = (roomId) => `/app/room/${roomId}/chat`
export const APP_ROOM_FILL = (roomId) =>`/app/room/${roomId}/fill`
export const APP_ROOM_START_DRAWING = (roomId) => `/app/room/${roomId}/startDrawing`
export const APP_ROOM_DRAW = (roomId) =>  `/app/room/${roomId}/draw`
export const APP_ROOM_STOP_DRAWING = (roomId) => `/app/room/${roomId}/stopDrawing`

export const USER_TOPIC_ROOM_CREATED = '/user/topic/roomCreated'
export const USER_TOPIC_NICKNAME = '/user/topic/nickname'
export const USER_TOPIC_WORD_OPTIONS = '/user/topic/wordOptions'
export const TOPIC_ROOMS = '/topic/rooms'
export const TOPIC_ROOM_CHAT = (roomId) => `/topic/room/${roomId}/chat`
export const TOPIC_ROOM_DRAWING = (roomId) => `/topic/room/${roomId}/drawing`
export const TOPIC_ROOM_CLEAR_CANVAS = (roomId) => `/topic/room/${roomId}/clearCanvas`
export const TOPIC_ROOM_PARTICIPANTS = (roomId) => `/topic/room/${roomId}/participants`
export const TOPIC_ROOM_WORD_HINT = (roomId) => `/topic/room/${roomId}/wordHint`