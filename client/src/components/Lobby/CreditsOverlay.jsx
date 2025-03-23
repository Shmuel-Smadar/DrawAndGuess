import React from 'react'
import './CreditsOverlay.css'

function CreditsOverlay({ onClose }) {
  return (
    <div className="credits-overlay">
      <div className="credits-modal">
        <button className="close-button" onClick={onClose}>X</button>
        <h2>Credits</h2>
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
