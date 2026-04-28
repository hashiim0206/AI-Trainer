'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import toast from 'react-hot-toast';

export default function Progress() {
  const [activeTab, setActiveTab] = useState<'weekly' | 'daily'>('weekly');
  const [history, setHistory] = useState<any[]>([]);
  const [dailyLogs, setDailyLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [latestResponse, setLatestResponse] = useState<any>(null);

  // Weekly Form State
  const [weeklyData, setWeeklyData] = useState({
    weightKg: '',
    bodyFatPercent: '',
    notes: ''
  });

  // Daily Form State
  const [dailyData, setDailyData] = useState({
    mealType: 'BREAKFAST',
    foodDescription: ''
  });

  useEffect(() => {
    fetchHistory();
    fetchDailyLogs();
  }, []);

  const fetchHistory = async () => {
    try {
      const data = await apiFetch('/progress/history');
      setHistory(data);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const fetchDailyLogs = async () => {
    const today = new Date().toISOString().split('T')[0];
    try {
      const data = await apiFetch(`/daily-log/${today}`);
      setDailyLogs(data);
    } catch (err) { console.error(err); }
  };

  const handleWeeklySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload: any = {
        weightKg: Number(weeklyData.weightKg),
        bodyFatPercent: weeklyData.bodyFatPercent ? Number(weeklyData.bodyFatPercent) : null,
        notes: weeklyData.notes
      };

      const res = await apiFetch('/progress/checkin', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      
      setLatestResponse(res);
      setWeeklyData({ weightKg: '', bodyFatPercent: '', notes: '' });
      toast.success('Weekly check-in logged!');
      fetchHistory();
    } catch (err) {
      toast.error('Failed to log check-in');
    } finally { setSubmitting(false); }
  };

  const handleDailySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!dailyData.foodDescription.trim()) return;
    setSubmitting(true);
    try {
      const today = new Date().toISOString().split('T')[0];
      await apiFetch('/daily-log', {
        method: 'POST',
        body: JSON.stringify({ ...dailyData, date: today })
      });
      setDailyData({ ...dailyData, foodDescription: '' });
      toast.success('Meal logged and calories calculated!');
      fetchDailyLogs();
    } catch (err) {
      toast.error('Could not analyze food. Try being more specific.');
    } finally { setSubmitting(false); }
  };

  const deleteDailyLog = async (id: number) => {
    try {
      await apiFetch(`/daily-log/${id}`, { method: 'DELETE' });
      fetchDailyLogs();
      toast.success('Log deleted');
    } catch (err) { toast.error('Failed to delete log'); }
  };

  const totalDailyCalories = dailyLogs.reduce((sum, log) => sum + (log.calories || 0), 0);

  if (loading) return <div className="container section"><div className="spinner"></div></div>;

  return (
    <div className="container section">
      <h1 className="heading-1" style={{ marginBottom: '32px' }}>Progress Tracking</h1>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '32px', borderBottom: '1px solid var(--border)', paddingBottom: '1px' }}>
        <button 
          onClick={() => setActiveTab('weekly')}
          className={`btn ${activeTab === 'weekly' ? 'btn-primary' : 'btn-ghost'}`}
          style={{ borderRadius: '8px 8px 0 0', padding: '12px 24px' }}
        >
          📅 Weekly Check-in
        </button>
        <button 
          onClick={() => setActiveTab('daily')}
          className={`btn ${activeTab === 'daily' ? 'btn-primary' : 'btn-ghost'}`}
          style={{ borderRadius: '8px 8px 0 0', padding: '12px 24px' }}
        >
          🥗 Daily Food Log
        </button>
      </div>

      <div className="form-row">
        {/* Left Column: Forms */}
        <div style={{ flex: '1.2' }}>
          
          {activeTab === 'weekly' && (
            <div className="anim-fade-up">
              {latestResponse && (
                <div className="card" style={{ marginBottom: '24px', background: 'var(--primary-glow)', borderColor: 'var(--primary)' }}>
                  <h3 className="heading-3">Week {latestResponse.weekNumber} Feedback</h3>
                  <p className="text-sm mt-2">"{latestResponse.aiMessage}"</p>
                </div>
              )}
              <div className="card">
                <h2 className="heading-2" style={{ marginBottom: '16px' }}>Weekly Check-in</h2>
                <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>Log your official weight and body fat for the week.</p>
                <form onSubmit={handleWeeklySubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div className="form-row">
                    <div className="form-group">
                      <label className="form-label">Weight (kg)</label>
                      <input type="number" step="0.1" className="form-input" value={weeklyData.weightKg} onChange={e => setWeeklyData({...weeklyData, weightKg: e.target.value})} required />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Body Fat % (Optional)</label>
                      <input type="number" step="0.1" className="form-input" value={weeklyData.bodyFatPercent} onChange={e => setWeeklyData({...weeklyData, bodyFatPercent: e.target.value})} />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Notes (Optional)</label>
                    <textarea className="form-input" rows={3} placeholder="How are you feeling this week?" value={weeklyData.notes} onChange={e => setWeeklyData({...weeklyData, notes: e.target.value})}></textarea>
                  </div>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'Logging...' : 'Log Weekly Stats'}
                  </button>
                </form>
              </div>
            </div>
          )}

          {activeTab === 'daily' && (
            <div className="anim-fade-up">
              <div className="card" style={{ marginBottom: '24px' }}>
                <h2 className="heading-2" style={{ marginBottom: '16px' }}>What did you eat today?</h2>
                <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>Just type the food naturally (e.g. "2 boiled eggs and a coffee"). AI will calculate the calories.</p>
                <form onSubmit={handleDailySubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div className="form-row">
                    <div className="form-group" style={{ flex: '0.4' }}>
                      <label className="form-label">Meal</label>
                      <select className="form-input" value={dailyData.mealType} onChange={e => setDailyData({...dailyData, mealType: e.target.value})}>
                        <option value="BREAKFAST">Breakfast</option>
                        <option value="BRUNCH">Brunch</option>
                        <option value="LUNCH">Lunch</option>
                        <option value="SNACK">Snack</option>
                        <option value="DINNER">Dinner</option>
                      </select>
                    </div>
                    <div className="form-group" style={{ flex: '1' }}>
                      <label className="form-label">Description</label>
                      <input type="text" className="form-input" placeholder="e.g. 150g chicken breast and 1 cup of rice" value={dailyData.foodDescription} onChange={e => setDailyData({...dailyData, foodDescription: e.target.value})} required />
                    </div>
                  </div>
                  <button type="submit" className="btn btn-primary" disabled={submitting}>
                    {submitting ? 'AI is analyzing...' : 'Log Meal'}
                  </button>
                </form>
              </div>

              {/* Today's Meals */}
              <div className="card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                  <h3 className="heading-3">Today's Logs</h3>
                  <div className="badge badge-accent" style={{ fontSize: '16px', padding: '8px 16px' }}>{totalDailyCalories} Total Calories</div>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                  {dailyLogs.length === 0 && <p className="text-muted">No meals logged for today yet.</p>}
                  {dailyLogs.map(log => (
                    <div key={log.id} style={{ padding: '16px', background: 'var(--bg-3)', borderRadius: '12px', border: '1px solid var(--border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <div style={{ flex: 1 }}>
                        <div style={{ display: 'flex', gap: '8px', alignItems: 'center', marginBottom: '4px' }}>
                          <span className="badge badge-primary text-xs">{log.mealType}</span>
                          <span className="font-bold">{log.calories} kcal</span>
                        </div>
                        <div className="text-sm text-muted">{log.foodDescription}</div>
                        <div style={{ display: 'flex', gap: '12px', marginTop: '8px', fontSize: '12px', color: 'var(--text-3)' }}>
                          <span>P: {log.protein}g</span>
                          <span>C: {log.carbs}g</span>
                          <span>F: {log.fat}g</span>
                        </div>
                      </div>
                      <button onClick={() => deleteDailyLog(log.id)} style={{ background: 'none', border: 'none', color: 'var(--red)', cursor: 'pointer', opacity: 0.6, fontSize: '20px' }}>×</button>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        {/* Right Column: Chart & History */}
        <div style={{ flex: '1' }}>
          <div className="card anim-fade-up" style={{ animationDelay: '0.1s' }}>
            <h2 className="heading-2" style={{ marginBottom: '24px' }}>Weight Trend</h2>
            {history.length < 2 ? (
              <p className="text-muted text-sm">Log at least 2 weekly check-ins to see your chart.</p>
            ) : (
              <div style={{ width: '100%', height: '240px' }}>
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={history}>
                    <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                    <XAxis dataKey="weekNumber" stroke="var(--text-3)" tickFormatter={(v) => `Wk ${v}`} />
                    <YAxis domain={['auto', 'auto']} hide />
                    <Tooltip contentStyle={{ background: 'var(--bg-3)', border: '1px solid var(--border)', borderRadius: '8px' }} />
                    <Line type="monotone" dataKey="weightKg" name="Weight" stroke="var(--primary-lt)" strokeWidth={3} dot={{ r: 4 }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            )}

            <div style={{ marginTop: '32px' }}>
              <h3 className="heading-3" style={{ marginBottom: '16px' }}>Past Check-ins</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {[...history].reverse().slice(0, 5).map((item, i) => (
                  <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--border)' }}>
                    <span className="text-sm">Week {item.weekNumber}</span>
                    <span className="font-bold">{item.weightKg} kg</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
