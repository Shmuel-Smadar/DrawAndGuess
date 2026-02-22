// src/components/common/AppHeader.jsx
import React from "react";
import { motion } from "framer-motion";

export default function AppHeader({
  title,
  left = null,
  right = null,
  className = "",
}) {
  return (
    <motion.header
      initial={{ opacity: 0, y: -6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.35 }}
      className={`max-w-7xl mx-auto px-2 lg:px-4 ${className}`}
    >
      <div className="grid grid-cols-[1fr_auto_1fr] items-center gap-2 py-1 min-h-[40px]">
        <div className="justify-self-start flex items-center gap-1">{left}</div>

        <h1 className="game-title justify-self-center">{title}</h1>

        <div className="justify-self-end flex items-center gap-1">{right}</div>
      </div>
    </motion.header>
  );
}
