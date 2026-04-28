'use client';

import { useState, useEffect } from 'react';
import { apiFetch, getToken } from '@/lib/api';
import toast from 'react-hot-toast';

export default function CalorieCalculator() {
  const [formData, setFormData] = useState({
    age: '',
    gender: 'MALE',
    weight: '',
    height: '',
    activityLevel: '1.2',
    goal: 'MAINTAIN'
  });

  const [isLoadingProfile, setIsLoadingProfile] = useState(false);

  useEffect(() => {
    loadSyncedProfile();
  }, []);

  const loadSyncedProfile = async () => {
    if (getToken()) {
      setIsLoadingProfile(true);
      try {
        const profile = await apiFetch('/profile/me');
        if (profile) {
          setFormData(prev => ({
            ...prev,
            age: profile.age.toString(),
            gender: profile.gender,
            weight: profile.weightKg.toString(),
            height: profile.heightCm.toString()
          }));
          toast.success('Stats synced with your latest progress!');
        }
      } catch (err) {
        console.error("Profile load failed", err);
      } finally {
        setIsLoadingProfile(false);
      }
    }
  };

  const [results, setResults] = useState<{
    bmr: number;
    tdee: number;
    targetCalories: number;
    protein: number;
    carbs: number;
    fats: number;
  } | null>(null);

  const calculate = (e: React.FormEvent) => {
    e.preventDefault();
    const weight = Number(formData.weight);
    const height = Number(formData.height);
    const age = Number(formData.age);
    const activityFactor = Number(formData.activityLevel);

    if (!weight || !height || !age) return;

    // 1. Calculate BMR (Mifflin-St Jeor Equation)
    let bmr = (10 * weight) + (6.25 * height) - (5 * age);
    if (formData.gender === 'MALE') {
      bmr += 5;
    } else {
      bmr -= 161;
    }

    // 2. Calculate TDEE (Total Daily Energy Expenditure)
    const tdee = bmr * activityFactor;

    // 3. Adjust for Goal
    let targetCalories = tdee;
    if (formData.goal === 'LOSE') targetCalories -= 500; // 500 cal deficit
    if (formData.goal === 'GAIN') targetCalories += 300; // 300 cal surplus

    // 4. Calculate Macros (Standard balanced diet: 30% P, 40% C, 30% F)
    const proteinCals = targetCalories * 0.3;
    const carbCals = targetCalories * 0.4;
    const fatCals = targetCalories * 0.3;

    setResults({
      bmr: Math.round(bmr),
      tdee: Math.round(tdee),
      targetCalories: Math.round(targetCalories),
      protein: Math.round(proteinCals / 4),
      carbs: Math.round(carbCals / 4),
      fats: Math.round(fatCals / 9)
    });
  };

  return (
    <div className="container section" style={{ display: 'flex', gap: '32px', flexWrap: 'wrap', alignItems: 'flex-start' }}>
      
      <div className="card anim-fade-up" style={{ flex: '1 1 400px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
          <h1 className="heading-1">Macro Calculator</h1>
          <button onClick={loadSyncedProfile} disabled={isLoadingProfile} className="text-xs text-accent" style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>
            {isLoadingProfile ? 'Syncing...' : '🔄 Refresh from Profile'}
          </button>
        </div>
        <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>
          Your weight and height are automatically synced with your latest weekly progress.
        </p>

        <form onSubmit={calculate} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {/* Form fields (Same as before but with better labels) */}
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Age</label>
              <input type="number" className="form-input" value={formData.age} onChange={e => setFormData({...formData, age: e.target.value})} required />
            </div>
            <div className="form-group">
              <label className="form-label">Gender</label>
              <select className="form-input" value={formData.gender} onChange={e => setFormData({...formData, gender: e.target.value})}>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label className="form-label">Weight (kg)</label>
              <input type="number" step="0.1" className="form-input" value={formData.weight} onChange={e => setFormData({...formData, weight: e.target.value})} required />
            </div>
            <div className="form-group">
              <label className="form-label">Height (cm)</label>
              <input type="number" step="0.1" className="form-input" value={formData.height} onChange={e => setFormData({...formData, height: e.target.value})} required />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Activity Level</label>
            <select className="form-input" value={formData.activityLevel} onChange={e => setFormData({...formData, activityLevel: e.target.value})}>
              <option value="1.2">Sedentary (office job, little to no exercise)</option>
              <option value="1.375">Lightly Active (1-3 days/week exercise)</option>
              <option value="1.55">Moderately Active (3-5 days/week exercise)</option>
              <option value="1.725">Very Active (6-7 days/week hard exercise)</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Your Goal</label>
            <select className="form-input" value={formData.goal} onChange={e => setFormData({...formData, goal: e.target.value})}>
              <option value="LOSE">Lose Weight (-500 cal/day)</option>
              <option value="MAINTAIN">Maintain Weight</option>
              <option value="GAIN">Build Muscle (+300 cal/day)</option>
            </select>
          </div>

          <button type="submit" className="btn btn-primary" style={{ marginTop: '8px' }}>Calculate My Macros</button>
        </form>
      </div>

      {results && (
        <div className="card anim-fade-up card-glow" style={{ flex: '1 1 300px', background: 'var(--primary-glow)', borderColor: 'var(--primary)' }}>
          <h2 className="heading-2" style={{ marginBottom: '24px' }}>Your Results</h2>
          
          <div style={{ textAlign: 'center', marginBottom: '32px' }}>
            <div className="text-muted text-sm text-uppercase" style={{ letterSpacing: '0.05em', fontWeight: 'bold' }}>Daily Target</div>
            <div className="heading-display text-gradient" style={{ fontSize: '48px' }}>{results.targetCalories}</div>
            <div className="text-muted">calories / day</div>
          </div>

          <div style={{ display: 'flex', gap: '12px', marginBottom: '32px' }}>
            <div className="stat-card" style={{ flex: 1, background: 'var(--bg-3)', alignItems: 'center' }}>
              <span className="stat-label">Protein</span>
              <span className="stat-value" style={{ fontSize: '20px', color: 'var(--primary-lt)' }}>{results.protein}g</span>
            </div>
            <div className="stat-card" style={{ flex: 1, background: 'var(--bg-3)', alignItems: 'center' }}>
              <span className="stat-label">Carbs</span>
              <span className="stat-value" style={{ fontSize: '20px', color: 'var(--accent-lt)' }}>{results.carbs}g</span>
            </div>
            <div className="stat-card" style={{ flex: 1, background: 'var(--bg-3)', alignItems: 'center' }}>
              <span className="stat-label">Fats</span>
              <span className="stat-value" style={{ fontSize: '20px', color: 'var(--amber)' }}>{results.fats}g</span>
            </div>
          </div>

          <div className="divider"></div>
          
          <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-2)', fontSize: '14px' }}>
            <span>Base Metabolic Rate (BMR)</span>
            <span style={{ color: 'var(--text)' }}>{results.bmr} kcal</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--text-2)', fontSize: '14px', marginTop: '8px' }}>
            <span>Maintenance Calories (TDEE)</span>
            <span style={{ color: 'var(--text)' }}>{results.tdee} kcal</span>
          </div>
        </div>
      )}
    </div>
  );
}
