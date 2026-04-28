'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { getToken, removeToken } from '@/lib/api';

export default function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const [isAuth, setIsAuth] = useState(false);
  const [isDark, setIsDark] = useState(true);

  useEffect(() => {
    setIsAuth(!!getToken());
    // Load saved theme
    const saved = localStorage.getItem('theme');
    if (saved === 'light') {
      setIsDark(false);
      document.documentElement.setAttribute('data-theme', 'light');
    }
  }, [pathname]);

  const toggleTheme = () => {
    const next = !isDark;
    setIsDark(next);
    const theme = next ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  };

  const handleLogout = () => {
    removeToken();
    setIsAuth(false);
    router.push('/auth/login');
  };

  return (
    <nav style={{ background: 'var(--glass)', borderBottom: '1px solid var(--border)', padding: '16px 0', backdropFilter: 'blur(12px)', position: 'sticky', top: 0, zIndex: 100 }}>
      <div className="container" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Link href="/" className="heading-3" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span className="text-gradient">AI Trainer</span>
        </Link>

        <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
          {isAuth ? (
            <>
              <Link href="/dashboard" style={{ color: pathname === '/dashboard' ? 'var(--primary-lt)' : 'var(--text-2)', transition: 'color var(--transition)' }}>Dashboard</Link>
              <Link href="/chat" style={{ color: pathname === '/chat' ? 'var(--primary-lt)' : 'var(--text-2)', transition: 'color var(--transition)' }}>Coach Chat</Link>
              <Link href="/progress" style={{ color: pathname === '/progress' ? 'var(--primary-lt)' : 'var(--text-2)', transition: 'color var(--transition)' }}>Progress</Link>
              <Link href="/profile" style={{ color: pathname === '/profile' ? 'var(--primary-lt)' : 'var(--text-2)', transition: 'color var(--transition)' }}>Profile</Link>
              <Link href="/settings" style={{ color: pathname === '/settings' ? 'var(--primary-lt)' : 'var(--text-2)', transition: 'color var(--transition)' }}>Settings</Link>
              <button
                onClick={toggleTheme}
                title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
                style={{
                  background: 'none', border: '1px solid var(--border)',
                  borderRadius: '8px', color: 'var(--text-2)',
                  cursor: 'pointer', fontSize: '18px',
                  padding: '4px 8px', lineHeight: 1,
                  transition: 'all var(--transition)'
                }}
              >
                {isDark ? '☀️' : '🌙'}
              </button>
              <button onClick={handleLogout} className="btn btn-ghost btn-sm" style={{ marginLeft: '4px' }}>Log Out</button>
            </>
          ) : (
            <>
              <button
                onClick={toggleTheme}
                title={isDark ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
                style={{
                  background: 'none', border: '1px solid var(--border)',
                  borderRadius: '8px', color: 'var(--text-2)',
                  cursor: 'pointer', fontSize: '18px',
                  padding: '4px 8px', lineHeight: 1
                }}
              >
                {isDark ? '☀️' : '🌙'}
              </button>
              <Link href="/auth/login" className="btn btn-ghost btn-sm">Log In</Link>
              <Link href="/auth/register" className="btn btn-primary btn-sm">Get Started</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
