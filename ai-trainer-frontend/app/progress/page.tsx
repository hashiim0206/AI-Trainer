'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import toast from 'react-hot-toast';

export default function Progress() {
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [latestResponse, setLatestResponse] = useState<any>(null);

  const [formData, setFormData] = useState({
    weightKg: '',
    bodyFatPercent: '',
    energyLevel: '5',
    caloriesConsumed: '',
    proteinConsumed: '',
    carbsConsumed: '',
    fatConsumed: '',
    workoutCompleted: false,
    dietCompleted: false,
    notes: ''
  });

  useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const data = await apiFetch('/progress/history');
      setHistory(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload: any = {
        weightKg: Number(formData.weightKg),
        energyLevel: Number(formData.energyLevel),
        workoutCompleted: formData.workoutCompleted,
        dietCompleted: formData.dietCompleted
      };
      if (formData.bodyFatPercent) payload.bodyFatPercent = Number(formData.bodyFatPercent);
      if (formData.caloriesConsumed) payload.caloriesConsumed = Number(formData.caloriesConsumed);
      if (formData.proteinConsumed) payload.proteinConsumed = Number(formData.proteinConsumed);
      if (formData.carbsConsumed) payload.carbsConsumed = Number(formData.carbsConsumed);
      if (formData.fatConsumed) payload.fatConsumed = Number(formData.fatConsumed);
      if (formData.notes) payload.notes = formData.notes;

      const res = await apiFetch('/progress/checkin', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      
      setLatestResponse(res);
      setFormData({ 
        weightKg: '', bodyFatPercent: '', energyLevel: '5', notes: '',
        caloriesConsumed: '', proteinConsumed: '', carbsConsumed: '', fatConsumed: '',
        workoutCompleted: false, dietCompleted: false
      });
      toast.success('Check-in logged successfully!');
      fetchHistory(); // Refresh chart
    } catch (err) {
      console.error(err);
      toast.error('Failed to log check-in');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <div className="container section"><div className="spinner"></div></div>;

  return (
    <div className="container section">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <h1 className="heading-1">Progress Tracking</h1>
      </div>

      <div className="form-row">
        {/* Left Column: Form & Latest AI Message */}
        <div>
          {latestResponse && (
            <div className="card anim-fade-up" style={{ marginBottom: '24px', background: 'var(--accent-glow)', borderColor: 'var(--accent)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '12px' }}>
                <span className="badge badge-accent">Week {latestResponse.weekNumber} Results</span>
                <span className="text-sm font-bold">{latestResponse.trend}</span>
              </div>
              <h3 className="heading-3">{latestResponse.trendMessage}</h3>
              <p className="text-muted text-sm" style={{ marginTop: '12px', fontStyle: 'italic' }}>
                " {latestResponse.aiMessage} "
              </p>
            </div>
          )}

          <div className="card anim-fade-up">
            <h2 className="heading-2" style={{ marginBottom: '16px' }}>Weekly Check-in</h2>
            <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Weight (kg)</label>
                  <input type="number" step="0.1" className="form-input" value={formData.weightKg} onChange={e => setFormData({...formData, weightKg: e.target.value})} required />
                </div>
                <div className="form-group">
                  <label className="form-label">Body Fat % (Optional)</label>
                  <input type="number" step="0.1" className="form-input" value={formData.bodyFatPercent} onChange={e => setFormData({...formData, bodyFatPercent: e.target.value})} />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Energy Level (1-10)</label>
                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                  <input type="range" min="1" max="10" className="form-input" style={{ flex: 1, padding: 0 }} value={formData.energyLevel} onChange={e => setFormData({...formData, energyLevel: e.target.value})} />
                  <span className="badge badge-primary">{formData.energyLevel}/10</span>
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Calories Consumed</label>
                  <input type="number" className="form-input" placeholder="e.g. 2100" value={formData.caloriesConsumed} onChange={e => setFormData({...formData, caloriesConsumed: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Protein (g)</label>
                  <input type="number" className="form-input" placeholder="e.g. 150" value={formData.proteinConsumed} onChange={e => setFormData({...formData, proteinConsumed: e.target.value})} />
                </div>
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label className="form-label">Carbs (g)</label>
                  <input type="number" className="form-input" placeholder="e.g. 200" value={formData.carbsConsumed} onChange={e => setFormData({...formData, carbsConsumed: e.target.value})} />
                </div>
                <div className="form-group">
                  <label className="form-label">Fat (g)</label>
                  <input type="number" className="form-input" placeholder="e.g. 60" value={formData.fatConsumed} onChange={e => setFormData({...formData, fatConsumed: e.target.value})} />
                </div>
              </div>

              <div className="form-row" style={{ alignItems: 'center', gap: '16px' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                  <input type="checkbox" checked={formData.workoutCompleted} onChange={e => setFormData({...formData, workoutCompleted: e.target.checked})} style={{ width: '18px', height: '18px' }} />
                  <span className="form-label" style={{ marginBottom: 0 }}>Workout Completed</span>
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                  <input type="checkbox" checked={formData.dietCompleted} onChange={e => setFormData({...formData, dietCompleted: e.target.checked})} style={{ width: '18px', height: '18px' }} />
                  <span className="form-label" style={{ marginBottom: 0 }}>Diet Followed</span>
                </label>
              </div>

              <div className="form-group">
                <label className="form-label">Notes (Optional)</label>
                <textarea className="form-input" rows={2} placeholder="How was your week? Did you stick to the plan?" value={formData.notes} onChange={e => setFormData({...formData, notes: e.target.value})}></textarea>
              </div>

              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? <span className="spinner"></span> : 'Log Check-in'}
              </button>
            </form>
          </div>
        </div>

        {/* Right Column: Chart */}
        <div className="card anim-fade-up" style={{ animationDelay: '0.2s', display: 'flex', flexDirection: 'column' }}>
          <h2 className="heading-2" style={{ marginBottom: '24px' }}>Weight Trend</h2>
          
          {history.length < 2 ? (
            <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text-3)', textAlign: 'center' }}>
              Log at least 2 check-ins to see your progress chart.
            </div>
          ) : (
            <div style={{ width: '100%', height: '300px' }}>
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={history} margin={{ top: 5, right: 20, bottom: 5, left: 0 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" vertical={false} />
                  <XAxis dataKey="weekNumber" stroke="var(--text-3)" tickFormatter={(v) => `Wk ${v}`} />
                  <YAxis domain={['auto', 'auto']} stroke="var(--text-3)" />
                  <Tooltip 
                    contentStyle={{ background: 'var(--bg-3)', border: '1px solid var(--border)', borderRadius: '8px' }}
                    itemStyle={{ color: 'var(--text)' }}
                    labelFormatter={(v) => `Week ${v}`}
                  />
                  <Line type="monotone" dataKey="weightKg" name="Weight (kg)" stroke="var(--primary-lt)" strokeWidth={3} dot={{ r: 5, fill: 'var(--primary)' }} activeDot={{ r: 8 }} />
                </LineChart>
              </ResponsiveContainer>
            </div>
          )}

          <div style={{ marginTop: '24px' }}>
            <h3 className="heading-3" style={{ marginBottom: '12px' }}>History Logs</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {[...history].reverse().map((item, idx) => (
                <div key={idx} style={{ padding: '16px', background: 'var(--bg-3)', borderRadius: '8px', border: '1px solid var(--border)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                    <div>
                      <div className="font-bold">Week {item.weekNumber}</div>
                      <div className="text-xs text-muted">{new Date(item.date).toLocaleDateString()}</div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div className="font-bold">{item.weightKg} kg</div>
                      <div className={`text-xs ${item.changeFromStart < 0 ? 'text-accent' : (item.changeFromStart > 0 ? 'text-red' : 'text-muted')}`}>
                        {item.changeFromStart > 0 ? '+' : ''}{item.changeFromStart} kg total
                      </div>
                    </div>
                  </div>
                  {(item.caloriesConsumed || item.workoutCompleted || item.dietCompleted) && (
                    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '8px' }}>
                      {item.caloriesConsumed && <span className="badge badge-primary">{item.caloriesConsumed} kcal</span>}
                      {item.proteinConsumed && <span className="badge badge-accent">{item.proteinConsumed}g protein</span>}
                      {item.workoutCompleted && <span className="badge badge-green">💪 Workout Done</span>}
                      {item.dietCompleted && <span className="badge badge-blue">🥗 Diet Followed</span>}
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
