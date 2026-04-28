'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import Link from 'next/link';

export default function Dashboard() {
  const [profile, setProfile] = useState<any>(null);
  const [stats, setStats] = useState<any>(null);
  const [plan, setPlan] = useState<any>(null);
  const [projection, setProjection] = useState<any>(null);
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
      const [profileData, planData, projectionData] = await Promise.all([
        apiFetch('/profile/me').catch(() => null),
        apiFetch('/plan/my-plan').catch(() => null),
        apiFetch('/progress/projection').catch(() => null)
      ]);
      
      if (profileData) {
        setProfile(profileData);
        setStats(profileData.stats);
      }
      if (planData) setPlan(planData);
      if (projectionData) setProjection(projectionData);
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
          
          {/* Motivation Banner */}
          <div className="card card-glow" style={{ marginBottom: '32px', background: 'var(--primary-glow)', padding: '20px', borderLeft: '5px solid var(--primary)' }}>
             <p className="font-bold text-gradient" style={{ fontSize: '20px' }}>
                {stats?.motivationalMessage || "Ready to crush your goals today?"}
             </p>
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
            <h2 className="heading-2">Welcome back, {profile.fullName.split(' ')[0]}</h2>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'var(--bg-3)', padding: '8px 16px', borderRadius: '99px', border: '1px solid var(--border)' }}>
              <span style={{ fontSize: '20px' }}>🔥</span>
              <span className="font-bold">{profile.currentStreak || 1} Day Streak</span>
            </div>
          </div>
          
          <div className="form-row" style={{ marginBottom: '32px' }}>
            {/* Goal Card */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px', flex: 1.2 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <h3 className="heading-3 text-gradient">Current Goal</h3>
                <div className="badge badge-accent">Target: {profile.targetWeightKg}kg</div>
              </div>
              <p className="font-bold">{plan ? plan.goal : 'No goal set yet.'}</p>
              
              {projection && projection.weeksToGoal && (
                <div style={{ background: 'var(--bg-3)', padding: '12px', borderRadius: '8px', border: '1px solid var(--border)' }}>
                  <div className="text-xs text-muted mb-1">Estimated Goal Arrival</div>
                  <div className="font-bold text-accent" style={{ fontSize: '18px' }}>
                    {new Date(projection.projectedDate).toLocaleDateString(undefined, { month: 'long', day: 'numeric', year: 'numeric' })}
                  </div>
                  <div className="text-xs text-muted mt-1">{projection.weeksToGoal} weeks remaining at current pace</div>
                </div>
              )}

              <div style={{ marginTop: 'auto' }}>
                <Link href="/plan" className="btn btn-primary btn-sm">{plan ? 'View Plan' : 'Generate Plan'}</Link>
              </div>
            </div>

            {/* Nutrition Card */}
            <div className="card" style={{ display: 'flex', flexDirection: 'column', gap: '16px', flex: 1 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <h3 className="heading-3 text-gradient">Daily Targets</h3>
                <div className="text-xs text-muted">Ideal: {stats?.healthyWeightMin}-{stats?.healthyWeightMax}kg</div>
              </div>
              
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', padding: '8px', background: 'var(--bg-3)', borderRadius: '8px' }}>
                  <span className="text-sm font-bold">Calories</span>
                  <span className="text-sm text-accent">{Math.round(stats?.maintenanceCalories)} kcal</span>
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
                   <div style={{ textAlign: 'center', background: 'var(--bg-3)', padding: '8px', borderRadius: '8px' }}>
                      <div className="text-xs text-muted">Protein</div>
                      <div className="font-bold text-sm">{stats?.targetProtein}g</div>
                   </div>
                   <div style={{ textAlign: 'center', background: 'var(--bg-3)', padding: '8px', borderRadius: '8px' }}>
                      <div className="text-xs text-muted">Carbs</div>
                      <div className="font-bold text-sm">{stats?.targetCarbs}g</div>
                   </div>
                   <div style={{ textAlign: 'center', background: 'var(--bg-3)', padding: '8px', borderRadius: '8px' }}>
                      <div className="text-xs text-muted">Fat</div>
                      <div className="font-bold text-sm">{stats?.targetFat}g</div>
                   </div>
                </div>
              </div>

              <div style={{ marginTop: 'auto' }}>
                <Link href="/profile" className="btn btn-ghost btn-sm">Adjust Profile</Link>
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
