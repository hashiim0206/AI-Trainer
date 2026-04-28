'use client';

import { useState, useEffect } from 'react';

export default function WorkoutTimer() {
  const [isOpen, setIsOpen] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);
  const [isActive, setIsActive] = useState(false);
  const [customMinutes, setCustomMinutes] = useState('1');

  useEffect(() => {
    let interval: NodeJS.Timeout | null = null;
    
    if (isActive && timeLeft > 0) {
      interval = setInterval(() => {
        setTimeLeft(time => time - 1);
      }, 1000);
    } else if (timeLeft === 0 && isActive) {
      setIsActive(false);
      // Play a simple beep sound when timer ends
      try {
        const audio = new Audio('data:audio/wav;base64,UklGRl9vT19XQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YU');
        audio.play().catch(() => {}); // Catch autoplay restrictions
      } catch (e) {}
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification('Timer Complete!', { body: 'Time to start your next set! 💪' });
      }
    }
    
    return () => {
      if (interval) clearInterval(interval);
    };
  }, [isActive, timeLeft]);

  const toggleTimer = () => setIsOpen(!isOpen);

  const startTimer = (seconds: number) => {
    setTimeLeft(seconds);
    setIsActive(true);
    setIsOpen(true);
  };

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s < 10 ? '0' : ''}${s}`;
  };

  return (
    <div style={{ position: 'fixed', bottom: '24px', right: '24px', zIndex: 999, display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: '12px' }}>
      
      {/* Timer Panel */}
      {isOpen && (
        <div className="card anim-fade-up card-glow" style={{ width: '280px', padding: '20px', background: 'var(--bg)', border: '1px solid var(--primary-lt)', boxShadow: 'var(--shadow-glow)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h3 className="heading-3">Rest Timer</h3>
            <button onClick={toggleTimer} style={{ background: 'none', border: 'none', color: 'var(--text-2)', cursor: 'pointer', fontSize: '20px' }}>×</button>
          </div>

          <div style={{ textAlign: 'center', marginBottom: '20px' }}>
            <div className="heading-display text-gradient" style={{ fontSize: '48px', fontFamily: 'monospace' }}>
              {formatTime(timeLeft)}
            </div>
            {isActive && <div className="text-muted text-sm anim-pulse">Resting...</div>}
            {!isActive && timeLeft > 0 && <div className="text-muted text-sm">Paused</div>}
            {!isActive && timeLeft === 0 && <div className="text-accent text-sm font-bold">Ready!</div>}
          </div>

          {/* Quick Starts */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px', marginBottom: '16px' }}>
            <button className="btn btn-ghost btn-sm" onClick={() => startTimer(30)}>30s</button>
            <button className="btn btn-ghost btn-sm" onClick={() => startTimer(60)}>60s</button>
            <button className="btn btn-ghost btn-sm" onClick={() => startTimer(90)}>90s</button>
            <button className="btn btn-ghost btn-sm" onClick={() => startTimer(120)}>2m</button>
          </div>

          {/* Controls */}
          <div style={{ display: 'flex', gap: '8px' }}>
            {isActive ? (
              <button className="btn btn-ghost" style={{ flex: 1, justifyContent: 'center' }} onClick={() => setIsActive(false)}>Pause</button>
            ) : (
              <button className="btn btn-primary" style={{ flex: 1, justifyContent: 'center' }} onClick={() => timeLeft > 0 ? setIsActive(true) : startTimer(60)} disabled={timeLeft === 0 && !isActive ? false : false}>
                {timeLeft > 0 ? 'Resume' : 'Start'}
              </button>
            )}
            <button className="btn btn-ghost" style={{ width: '40px', justifyContent: 'center', padding: 0 }} onClick={() => { setIsActive(false); setTimeLeft(0); }} title="Reset">
              ↺
            </button>
          </div>
        </div>
      )}

      {/* Floating Button */}
      <button 
        onClick={toggleTimer}
        style={{
          width: '56px', height: '56px', borderRadius: '50%',
          background: isActive ? 'var(--accent)' : 'var(--primary)',
          color: 'white', border: 'none', cursor: 'pointer',
          boxShadow: '0 4px 16px rgba(0,0,0,0.3)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          fontSize: '24px', transition: 'transform 0.2s'
        }}
        title="Workout Timer"
      >
        {isActive ? formatTime(timeLeft) : '⏱️'}
      </button>
    </div>
  );
}
