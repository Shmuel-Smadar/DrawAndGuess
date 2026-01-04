/*
 * Various constants used throughout the application for sizing, text,
 * colors, and so forth.
 */

export const VIRTUAL_WIDTH = 10
export const VIRTUAL_HEIGHT = 16
export const ASPECT_RATIO = VIRTUAL_WIDTH / VIRTUAL_HEIGHT
export const DEFAULT_SOCKET_URL = "http://localhost:8080/draw-and-guess"
export const DEFAULT_LEADERBOARD_URL = "http://localhost:8080/leaderboard"
export const LEADERBOARD_REFRESH_INTERVAL = 100
export const MAX_WINNER_MESSAGE_LENGTH = 50
export const CANVAS_WIDTH_RATIO = 0.9
export const CANVAS_HEIGHT_RATIO = 0.8
export const CHAT_HEIGHT_RATIO = 0.5
export const PARTICIPANTS_HEIGHT_RATIO = 0.5
export const FIRST_PLACE_ICON = "🏆"
export const SECOND_PLACE_ICON = "🥈"
export const THIRD_PLACE_ICON = "🥉"
export const BUCKET_ICON_CURSOR_OFFSET = "16 16"
export const DEFAULT_CURSOR = "crosshair"
export const GAME_TITLE = "What's Being Drawn?"
export const CLOSE_BUTTON_TEXT = "X"
export const NICKNAME_PROMPT_TITLE = "Enter Your Nickname"
export const NICKNAME_PLACEHOLDER = "Nickname"
export const JOIN_BUTTON_TEXT = "Join"
export const WINNER_PROMPT_TITLE = "You Won!"
export const WINNER_PROMPT_LABEL = "Add a message for the leaderboard:"
export const WINNER_PROMPT_SAVE = "Save"
export const WORD_SELECTION_TITLE = "Choose a Word to Draw"
export const LOBBY_TITLE = "Select a Room"
export const NEW_ROOM_PLACEHOLDER = "New Room Name"
export const CREATE_ROOM_BUTTON_TEXT = "Create Room & Join"
export const ROOM_NAME_EMPTY_ERROR = "Room name cannot be empty."
export const SERVER_CONNECTION_ERROR = "Unable to connect to the server."
export const EMPTY_ROOM_HEADING = "No Rooms Found"
export const EMPTY_ROOM_TEXT = "Looks like there are no rooms available right now. Create a new room and invite your friends to join!"
export const JOIN_ROOM_BUTTON_TEXT = "Join"
export const CHAT_TITLE = "Chat"
export const SCROLL_BUTTON_LABEL = "Scroll to the latest messages"
export const NEW_MESSAGES_LABEL = "New Messages"
export const CHAT_PLACEHOLDER = "Type a message..."
export const SEND_BUTTON_TEXT = "Send"
export const PARTICIPANTS_TITLE = "Participants"
export const DRAWING_INDICATOR_TEXT = " (Drawing)"
export const CREDITS_TITLE = "Credits"


export const WINNER_MESSAGE_MAX_LENGTH = 50;
export const MAX_CHAT_MESSAGE_LENGTH = 50;
export const MAX_NICKNAME_LENGTH = 20
export const MAX_ROOM_NAME_LENGTH = 20
export const NICKNAME_INVALID_ERROR = "Invalid nickname"
export const ROOM_NAME_MAX_ERROR = `Room name must not be longer than ${MAX_ROOM_NAME_LENGTH} characters.`

export const EVENT_TYPE_START = 'START'
export const EVENT_TYPE_DRAW = 'DRAW'
export const EVENT_TYPE_STOP = 'STOP'
export const EVENT_TYPE_FILL = 'FILL'

export const SYSTEM_MESSAGE_COLORS = {
  PARTICIPANT_JOINED: 'forestgreen',
  PARTICIPANT_LEFT: 'crimson',
  WORD_GUESSED: 'darkorange',
  NO_GUESS: 'mediumpurple',
  GAME_ENDED: 'darkslategray',
  PREVIOUS_DRAWER_QUIT: 'firebrick',
  ROUND_STARTED: 'royalblue',
  NEW_GAME_STARTED: 'seagreen'
}

export const COLOR_OPTIONS = [
  { code: '#000000' },
  { code: '#FF0000' },
  { code: '#00FF00' },
  { code: '#0000FF' },
  { code: '#FFFF00' },
  { code: '#A52A2A' },
  { code: '#800080' },
  { code: '#FFA500' },
  { code: '#FFC0CB' },
  { code: '#808080' },
  { code: '#00FFFF' },
  { code: '#FF00FF' },
  { code: '#8950F7' }
]

export const BRUSH_SIZES = [
  { name: 'S', size: 2 },
  { name: 'M', size: 5 },
  { name: 'L', size: 10 }
]


