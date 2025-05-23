import React from 'react'
import { CLOSE_BUTTON_TEXT, CREDITS_TITLE } from '../../utils/constants'
import './CreditsOverlay.css'

/*A simple overlay that shows credits for the icon creators  */
function CreditsOverlay({ onClose }) {
  return (
    <div className="credits-overlay">
      <div className="credits-modal">
        <button className="close-button" onClick={onClose}>{CLOSE_BUTTON_TEXT}</button>
        <h2>{CREDITS_TITLE}</h2>
        <ul className="credits-list">
          <li>
            <a href="https://www.flaticon.com/free-icons/paint-brush" title="paint brush icons" target="_blank" rel="noopener noreferrer">
              Paint brush icons created by Freepik - Flaticon
            </a>
          </li>
          <li>
            <a href="https://www.flaticon.com/free-icons/bin" title="bin icons" target="_blank" rel="noopener noreferrer">
              Bin icons created by Smashicons - Flaticon
            </a>
          </li>
          <li>
            <a href="https://www.flaticon.com/free-icons/paint-bucket" title="paint bucket icons" target="_blank" rel="noopener noreferrer">
              Paint bucket icons created by Freepik - Flaticon
            </a>
          </li>
          <li>
            <a href="https://www.flaticon.com/free-icons/no-money" title="no money icons" target="_blank" rel="noopener noreferrer">
              No money icons created by Chattapat - Flaticon
            </a>
          </li>
        </ul>
      </div>
    </div>
  )
}

export default CreditsOverlay
