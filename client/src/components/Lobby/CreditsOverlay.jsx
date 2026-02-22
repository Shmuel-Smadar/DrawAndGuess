import React from "react";
import { motion } from "framer-motion";
import { X, Info } from "lucide-react";
import { CREDITS_TITLE, CLOSE_BUTTON_TEXT } from "../../utils/constants";

function CreditsOverlay({ onClose }) {
  return (
    <div className="fixed inset-0 z-[1050] flex items-center justify-center p-4 bg-black/50">
      <motion.div
        initial={{ opacity: 0, scale: 0.97, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.98, y: 8 }}
        transition={{ duration: 0.2 }}
        className="card w-full max-w-xl max-h-[90vh] overflow-hidden"
        role="dialog"
        aria-modal="true"
        aria-label="Credits"
      >
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200 dark:border-gray-700 bg-white/80 dark:bg-gray-800/80">
          <div className="flex items-center gap-2">
            <Info className="w-5 h-5 text-primary-500" />
            <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
              {CREDITS_TITLE}
            </h2>
          </div>

          <button
            onClick={onClose}
            className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 transition-colors"
            aria-label={CLOSE_BUTTON_TEXT}
          >
            <X className="w-4 h-4 text-gray-700 dark:text-gray-200" />
          </button>
        </div>

        <div className="bg-gray-50 dark:bg-gray-900 px-5 py-4 overflow-y-auto max-h-[calc(90vh-64px)]">
          <ul className="space-y-3">
            <li className="rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3">
              <a
                href="https://www.flaticon.com/free-icons/paint-brush"
                title="paint brush icons"
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm font-medium text-primary-600 dark:text-primary-400 hover:underline"
              >
                Paint brush icons created by Freepik - Flaticon
              </a>
            </li>

            <li className="rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3">
              <a
                href="https://www.flaticon.com/free-icons/bin"
                title="bin icons"
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm font-medium text-primary-600 dark:text-primary-400 hover:underline"
              >
                Bin icons created by Smashicons - Flaticon
              </a>
            </li>

            <li className="rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3">
              <a
                href="https://www.flaticon.com/free-icons/paint-bucket"
                title="paint bucket icons"
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm font-medium text-primary-600 dark:text-primary-400 hover:underline"
              >
                Paint bucket icons created by Freepik - Flaticon
              </a>
            </li>

            <li className="rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3">
              <a
                href="https://www.flaticon.com/free-icons/no-money"
                title="no money icons"
                target="_blank"
                rel="noopener noreferrer"
                className="text-sm font-medium text-primary-600 dark:text-primary-400 hover:underline"
              >
                No money icons created by Chattapat - Flaticon
              </a>
            </li>
          </ul>
        </div>
      </motion.div>
    </div>
  );
}

export default CreditsOverlay;
