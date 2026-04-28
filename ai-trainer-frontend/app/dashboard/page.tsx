'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import Link from 'next/link';

export default function Dashboard() {
  const [profile, setProfile] = useState<any>(null);
  const [stats, setStats] = useState<any>(null);
  const [plan, setPlan] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
    setupNotifications();
  }, []);

  const setupNotifications = () => {
    if (!('Notification' in window)) return;
    
    if (Notification.permission === 'default') {
      Notification.requestPermission();
    } else if (Notification.permission === 'granted') {
      const lastNotified = localStorage.getItem('lastNotified');
      const now = new Date().getTime();
      // Notify if 24 hours have passed (86400000 ms)
      if (!lastNotified || now - Number(lastNotified) > 86400000) {
        new Notification('AI Trainer Reminder', {
          body: 'Don\'t forget to stay hydrated and log your meals today!',
          icon: '/favicon.ico'
        });
        localStorage.setItem('lastNotified', now.toString());
      }
    }
  };

  const fetchData = async () => {
    try {
      const [profileData, planData] = await Promise.all([
        apiFetch('/profile/me').catch(() => null),
        apiFetch('/plan/my-plan').catch(() => null)
      ]);
      
      if (profileData) {
        setProfile(profileData);
        setStats(profileData.stats);
      }
      if (planData) {
        setPlan(planData);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div className="container section"><div className="spinner"></div></div>;

  return (
    <div className="container section">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <h1 className="heading-1">Dashboard</h1>
      </div>

      {!profile && (
        <div className="card anim-fade-up" style={{ textAlign: 'center', padding: '48px 24px', background: 'var(--primary-glow)', borderColor: 'var(--primary)' }}>
          <h2 className="heading-2" style={{ marginBottom: '16px' }}>Welcome to AI Trainer!</h2>
          <p className="text-muted" style={{ marginBottom: '24px' }}>Your first step is to complete your health profile so the AI can understand your body.</p>
          <Link href="/profile" className="btn btn-primary">Complete Profile</Link>
        </div>
      )}

      {profile && (
        <div className="anim-fade-up">
          <h2 className="heading-2" style={{ marginBottom: '16px' }}>Welcome back, {profile.fullName.split(' ')[0]}</h2>
          
          <div className="form-row" style={{ marginBottom: '32px' }}>
            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <h3 className="heading-3 text-gradient">Your Current Goal</h3>
              <p className="text-muted text-sm">{plan ? plan.goal : 'No goal set yet.'}</p>
              <div style={{ marginTop: 'auto' }}>
                <Link href="/plan" className="btn btn-primary btn-sm">{plan ? 'View Plan' : 'Generate Plan'}</Link>
              </div>
            </div>

            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <h3 className="heading-3 text-gradient">Health Stats</h3>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                <span className="badge badge-accent">Weight: {profile.weightKg}kg</span>
                <span className="badge badge-primary">BMI: {stats?.bmi.toFixed(1)}</span>
                <span className="badge badge-amber">{Math.round(stats?.maintenanceCalories)} kcal/day</span>
              </div>
              <div style={{ marginTop: 'auto' }}>
                <Link href="/profile" className="btn btn-ghost btn-sm">Update Stats</Link>
              </div>
            </div>
          </div>

          <div className="form-row-3">
            <Link href="/chat" className="card card-glow" style={{ textDecoration: 'none' }}>
              <h3 className="heading-3" style={{ marginBottom: '8px' }}>💬 Chat with Coach</h3>
              <p className="text-muted text-sm">Ask questions, get motivation, and adjust your plan.</p>
            </Link>
            
            <Link href="/progress" className="card card-glow" style={{ textDecoration: 'none' }}>
              <h3 className="heading-3" style={{ marginBottom: '8px' }}>📈 Track Progress</h3>
              <p className="text-muted text-sm">Log your weekly check-in and view your trend charts.</p>
            </Link>

            <Link href="/plan" className="card card-glow" style={{ textDecoration: 'none' }}>
              <h3 className="heading-3" style={{ marginBottom: '8px' }}>⚡ Your Plan</h3>
              <p className="text-muted text-sm">View your personalized 7-day workout and diet plan.</p>
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
