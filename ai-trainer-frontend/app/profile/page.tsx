'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function Profile() {
  const router = useRouter();
  const [profile, setProfile] = useState<any>(null);
  const [stats, setStats] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [error, setError] = useState('');
  
  // Form state
  const [formData, setFormData] = useState({
    fullName: '',
    dateOfBirth: '',
    gender: 'MALE',
    heightCm: '',
    weightKg: '',
    targetWeightKg: '',
    trainingLevel: 'BEGINNER',
    dietPreference: 'ANY',
    sports: '',
    country: ''
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      setLoading(true);
      const data = await apiFetch('/profile/me');
      setProfile(data);
      setStats(data.stats);
      if (data) {
        setFormData({
          fullName: data.fullName || '',
          dateOfBirth: data.dateOfBirth || '',
          gender: data.gender || 'MALE',
          heightCm: data.heightCm || '',
          weightKg: data.weightKg || '',
          targetWeightKg: data.targetWeightKg || '',
          trainingLevel: data.trainingLevel || 'BEGINNER',
          dietPreference: data.dietPreference || 'ANY',
          sports: data.sports || '',
          country: data.country || ''
        });
      } else {
        setIsEditing(true); // No profile yet, force edit mode
      }
    } catch (err: any) {
      if (err.message.includes('Profile not found')) {
        setIsEditing(true);
      } else {
        setError(err.message || 'Failed to load profile');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    try {
      await apiFetch('/profile', {
        method: 'POST',
        body: JSON.stringify({
          ...formData,
          heightCm: Number(formData.heightCm),
          weightKg: Number(formData.weightKg),
          targetWeightKg: Number(formData.targetWeightKg)
        })
      });
      await fetchProfile();
      setIsEditing(false);
      toast.success('Profile saved successfully!');
    } catch (err: any) {
      setError(err.message || 'Failed to save profile');
      toast.error('Failed to save profile');
    }
  };

  if (loading) return <div className="container section"><div className="spinner"></div></div>;

  return (
    <div className="container section">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <h1 className="heading-1">Your Profile</h1>
        {profile && !isEditing && (
          <button onClick={() => setIsEditing(true)} className="btn btn-ghost">Edit Profile</button>
        )}
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '24px' }}>{error}</div>}

      {isEditing ? (
        <form onSubmit={handleSubmit} className="card anim-fade-up">
          <div className="form-row" style={{ marginBottom: '16px' }}>
            <div className="form-group">
              <label className="form-label">Full Name</label>
              <input type="text" className="form-input" value={formData.fullName} onChange={e => setFormData({...formData, fullName: e.target.value})} required />
            </div>
            <div className="form-group">
              <label className="form-label">Date of Birth</label>
              <input type="date" className="form-input" value={formData.dateOfBirth} onChange={e => setFormData({...formData, dateOfBirth: e.target.value})} required />
            </div>
          </div>

          <div className="form-row-3" style={{ marginBottom: '16px' }}>
            <div className="form-group">
              <label className="form-label">Gender</label>
              <select className="form-input" value={formData.gender} onChange={e => setFormData({...formData, gender: e.target.value})}>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Height (cm)</label>
              <input type="number" step="0.1" className="form-input" value={formData.heightCm} onChange={e => setFormData({...formData, heightCm: e.target.value})} required />
            </div>
            <div className="form-group">
              <label className="form-label">Current Weight (kg)</label>
              <input type="number" step="0.1" className="form-input" value={formData.weightKg} onChange={e => setFormData({...formData, weightKg: e.target.value})} required />
            </div>
          </div>

          <div className="form-row-3" style={{ marginBottom: '16px' }}>
            <div className="form-group">
              <label className="form-label">Target Weight (kg) 🎯</label>
              <input type="number" step="0.1" className="form-input" style={{ borderColor: 'var(--primary)', boxShadow: '0 0 10px var(--primary-glow)' }} value={formData.targetWeightKg} onChange={e => setFormData({...formData, targetWeightKg: e.target.value})} required placeholder="What weight are you aiming for?" />
            </div>
            <div className="form-group">
              <label className="form-label">Training Level</label>
              <select className="form-input" value={formData.trainingLevel} onChange={e => setFormData({...formData, trainingLevel: e.target.value})}>
                <option value="BEGINNER">Beginner</option>
                <option value="INTERMEDIATE">Intermediate</option>
                <option value="ADVANCED">Advanced</option>
                <option value="ATHLETE">Athlete</option>
              </select>
            </div>
            <div className="form-group">
              <label className="form-label">Diet Preference</label>
              <select className="form-input" value={formData.dietPreference} onChange={e => setFormData({...formData, dietPreference: e.target.value})}>
                <option value="ANY">Any</option>
                <option value="VEGETARIAN">Vegetarian</option>
                <option value="VEGAN">Vegan</option>
                <option value="KETO">Keto</option>
                <option value="PALEO">Paleo</option>
                <option value="HALAL">Halal</option>
              </select>
            </div>
          </div>

          <div className="form-row" style={{ marginBottom: '24px' }}>
            <div className="form-group">
              <label className="form-label">Country</label>
              <input type="text" className="form-input" value={formData.country} onChange={e => setFormData({...formData, country: e.target.value})} required placeholder="e.g. UK, USA" />
            </div>
            <div className="form-group">
              <label className="form-label">Sports / Activities</label>
              <input type="text" className="form-input" value={formData.sports} onChange={e => setFormData({...formData, sports: e.target.value})} placeholder="e.g. Football, Running (Optional)" />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '12px' }}>
            <button type="submit" className="btn btn-primary">Save Profile & Set Goal</button>
            {profile && <button type="button" onClick={() => setIsEditing(false)} className="btn btn-ghost">Cancel</button>}
          </div>
        </form>
      ) : profile ? (
        <div className="anim-fade-up">
          <div className="card" style={{ marginBottom: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
               <div>
                <h2 className="heading-2" style={{ marginBottom: '8px' }}>{profile.fullName}</h2>
                <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                  <span className="badge badge-primary">{profile.gender}</span>
                  <span className="badge badge-accent">{stats?.age} years old</span>
                  <span className="badge badge-amber">{profile.trainingLevel}</span>
                  <span className="badge badge-blue">{profile.dietPreference}</span>
                  {profile.sports && <span className="badge badge-primary">{profile.sports}</span>}
                  <span className="badge badge-accent">{profile.country}</span>
                </div>
               </div>
               <div style={{ textAlign: 'right' }}>
                 <div className="text-xs text-muted" style={{ marginBottom: '4px' }}>Target Goal</div>
                 <div className="heading-2 text-gradient">{profile.targetWeightKg} kg</div>
               </div>
            </div>
          </div>

          {stats && (
            <>
              <h3 className="heading-3" style={{ marginBottom: '16px', marginTop: '32px' }}>Your Health Stats</h3>
              <div className="form-row-3">
                <div className="stat-card">
                  <span className="stat-label">BMI</span>
                  <span className="stat-value">{stats.bmi.toFixed(1)}</span>
                  <span className="stat-sub">{stats.bmiCategory}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">Est. Body Fat</span>
                  <span className="stat-value">{stats.minBodyFatPercent.toFixed(1)}% - {stats.maxBodyFatPercent.toFixed(1)}%</span>
                  <span className="stat-sub">{stats.bodyFatCategory}</span>
                </div>
                <div className="stat-card">
                  <span className="stat-label">Daily Maintenance</span>
                  <span className="stat-value">{Math.round(stats.maintenanceCalories)}</span>
                  <span className="stat-sub">kcal / day</span>
                </div>
              </div>
            </>
          )}
        </div>
      ) : null}
    </div>
  );
}
