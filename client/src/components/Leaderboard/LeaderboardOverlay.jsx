import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { X, Trophy } from "lucide-react";
import {
  DEFAULT_LEADERBOARD_URL,
  LEADERBOARD_REFRESH_INTERVAL,
  FIRST_PLACE_ICON,
  SECOND_PLACE_ICON,
  THIRD_PLACE_ICON,
} from "../../utils/constants";

function MessageModal({ record, onClose }) {
  return (
    <div className="fixed inset-0 z-[1100] flex items-center justify-center p-4 bg-black/50">
      <motion.div
        initial={{ opacity: 0, scale: 0.96, y: 10 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.98, y: 8 }}
        transition={{ duration: 0.2 }}
        className="card w-full max-w-lg overflow-hidden"
        role="dialog"
        aria-modal="true"
        aria-label="Winner message"
      >
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200 dark:border-gray-700 bg-white/80 dark:bg-gray-800/80">
          <div className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-warning-500" />
            <h2 className="text-lg font-semibold text-gray-900 dark:text-gray-100">
              {record.username}&apos;s Message
            </h2>
          </div>

          <button
            onClick={onClose}
            className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 transition-colors"
            aria-label="Close"
          >
            <X className="w-4 h-4 text-gray-700 dark:text-gray-200" />
          </button>
        </div>

        <div className="px-5 py-4 bg-white dark:bg-gray-800">
          <p className="text-sm leading-relaxed text-gray-700 dark:text-gray-200 break-words">
            {record.message}
          </p>
        </div>
      </motion.div>
    </div>
  );
}

function LeaderboardOverlay({ onClose }) {
  const [scores, setScores] = useState([]);
  const [selectedRecord, setSelectedRecord] = useState(null);

  useEffect(() => {
    const fetchScores = async () => {
      try {
        const res = await fetch(
          process.env.REACT_APP_LEADERBOARD_URL || DEFAULT_LEADERBOARD_URL,
        );
        const data = await res.json();
        setScores(Array.isArray(data) ? data : []);
      } catch (err) {
        console.error(err);
      }
    };

    fetchScores();
    const interval = setInterval(fetchScores, LEADERBOARD_REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, []);

  const getRankIcon = (index) => {
    if (index === 0) return FIRST_PLACE_ICON;
    if (index === 1) return SECOND_PLACE_ICON;
    if (index === 2) return THIRD_PLACE_ICON;
    return null;
  };

  return (
    <>
      <div className="fixed inset-0 z-[1050] flex items-center justify-center p-4 bg-black/50">
        <motion.div
          initial={{ opacity: 0, scale: 0.97, y: 10 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.98, y: 8 }}
          transition={{ duration: 0.2 }}
          className="card w-full max-w-3xl max-h-[90vh] overflow-hidden"
          role="dialog"
          aria-modal="true"
          aria-label="Leaderboard"
        >
          <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200 dark:border-gray-700 bg-white/80 dark:bg-gray-800/80">
            <div className="flex items-center gap-2">
              <Trophy className="w-5 h-5 text-warning-500" />
              <h2 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
                Leaderboard
              </h2>
            </div>

            <button
              onClick={onClose}
              className="p-2 rounded-lg bg-gray-100 hover:bg-gray-200 dark:bg-gray-700 dark:hover:bg-gray-600 transition-colors"
              aria-label="Close"
            >
              <X className="w-4 h-4 text-gray-700 dark:text-gray-200" />
            </button>
          </div>

          <div className="bg-gray-50 dark:bg-gray-900 px-3 sm:px-5 py-4 overflow-y-auto max-h-[calc(90vh-64px)]">
            {scores.length === 0 ? (
              <div className="text-center py-10 text-gray-600 dark:text-gray-300">
                No scores yet.
              </div>
            ) : (
              <div className="space-y-2">
                {scores.map((item, index) => {
                  const clickable = Boolean(item?.message);
                  const medal = getRankIcon(index);

                  return (
                    <button
                      key={`${item?.username || "user"}-${index}`}
                      onClick={() => clickable && setSelectedRecord(item)}
                      className={`w-full text-left rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3 shadow-sm hover:shadow-md transition-all ${
                        clickable
                          ? "hover:bg-gray-50 dark:hover:bg-gray-700/60"
                          : "opacity-90 cursor-default"
                      }`}
                      type="button"
                      disabled={!clickable}
                    >
                      <div className="flex items-center gap-3">
                        <div className="flex items-center gap-2 min-w-[64px]">
                          <span className="font-bold text-gray-800 dark:text-gray-100">
                            {index + 1}.
                          </span>
                          {medal && (
                            <span className="text-lg leading-none">
                              {medal}
                            </span>
                          )}
                        </div>

                        <div className="flex-1 min-w-0">
                          <div className="font-medium text-gray-900 dark:text-gray-100 truncate">
                            {item.username}
                          </div>
                          {clickable && (
                            <div className="text-xs text-gray-500 dark:text-gray-400">
                              Tap to view message
                            </div>
                          )}
                        </div>

                        <div className="shrink-0 font-semibold text-success-600 dark:text-success-500">
                          {item.score} pts
                        </div>
                      </div>
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        </motion.div>
      </div>

      <AnimatePresence>
        {selectedRecord && (
          <MessageModal
            record={selectedRecord}
            onClose={() => setSelectedRecord(null)}
          />
        )}
      </AnimatePresence>
    </>
  );
}

export default LeaderboardOverlay;
