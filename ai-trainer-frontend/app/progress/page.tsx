'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import toast from 'react-hot-toast';

export default function Progress() {
  const [activeTab, setActiveTab] = useState<'weekly' | 'daily'>('weekly');
  const [history, setHistory] = useState<any[]>([]);
  const [dailyLogs, setDailyLogs] = useState<any[]>([]);
  const [projection, setProjection] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [latestResponse, setLatestResponse] = useState<any>(null);
  const [editingId, setEditingId] = useState<number | null>(null);

  // Weekly Form State
  const [weeklyData, setWeeklyData] = useState({
    weightKg: '',
    bodyFatPercent: '',
    notes: '',
    checkinDate: new Date().toISOString().split('T')[0]
  });

  // Daily Form State
  const [dailyData, setDailyData] = useState({
    mealType: 'BREAKFAST',
    foodDescription: ''
  });

  useEffect(() => {
    fetchHistory();
    fetchDailyLogs();
    fetchProjection();
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

  const fetchProjection = async () => {
    try {
      const data = await apiFetch('/progress/projection');
      setProjection(data);
    } catch (err) { console.error(err); }
  };

  const handleWeeklySubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const payload: any = {
        weightKg: Number(weeklyData.weightKg),
        bodyFatPercent: weeklyData.bodyFatPercent ? Number(weeklyData.bodyFatPercent) : null,
        notes: weeklyData.notes,
        checkinDate: weeklyData.checkinDate,
        energyLevel: 5
      };

      if (editingId) {
        await apiFetch(`/progress/${editingId}`, {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
        toast.success('Check-in updated!');
        setEditingId(null);
      } else {
        const res = await apiFetch('/progress/checkin', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
        setLatestResponse(res);
        toast.success('Weekly check-in logged!');
      }
      
      setWeeklyData({ 
        weightKg: '', 
        bodyFatPercent: '', 
        notes: '', 
        checkinDate: new Date().toISOString().split('T')[0] 
      });
      fetchHistory();
      fetchProjection();
    } catch (err) {
      toast.error(editingId ? 'Failed to update' : 'Failed to log check-in');
    } finally { setSubmitting(false); }
  };

  const deleteWeeklyEntry = async (id: number) => {
    if (!confirm('Are you sure you want to delete this check-in?')) return;
    try {
      await apiFetch(`/progress/${id}`, { method: 'DELETE' });
      toast.success('Entry deleted');
      fetchHistory();
      fetchProjection();
    } catch (err) { toast.error('Failed to delete entry'); }
  };

  const startEditing = (item: any) => {
    setEditingId(item.id);
    setWeeklyData({
      weightKg: item.weightKg.toString(),
      bodyFatPercent: item.bodyFatPercent ? item.bodyFatPercent.toString() : '',
      notes: '', // Notes aren't in history DTO currently
      checkinDate: item.date
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
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
              
              {/* SMART PROJECTION WIDGET */}
              {projection && projection.status !== 'NOT_ENOUGH_DATA' && (
                <div className="card card-glow" style={{ marginBottom: '24px', background: 'var(--primary-glow)', borderColor: 'var(--primary)' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                    <h3 className="heading-3">Smart Momentum Projection 🚀</h3>
                    <span className={`badge ${projection.status === 'LOSING' ? 'badge-accent' : 'badge-amber'}`}>
                      {projection.status}
                    </span>
                  </div>
                  
                  <div style={{ display: 'flex', gap: '24px', marginBottom: '16px' }}>
                    <div style={{ flex: 1 }}>
                      <div className="text-xs text-muted" style={{ marginBottom: '4px' }}>Avg. Weekly Change</div>
                      <div className="heading-2" style={{ color: projection.status === 'LOSING' ? 'var(--accent-lt)' : 'var(--red)' }}>
                        {projection.avgWeeklyChange > 0 ? '+' : ''}{projection.avgWeeklyChange} kg
                      </div>
                    </div>
                    {projection.weeksToGoal && (
                      <div style={{ flex: 1 }}>
                        <div className="text-xs text-muted" style={{ marginBottom: '4px' }}>Estimated Date</div>
                        <div className="heading-2 text-gradient">
                          {new Date(projection.projectedDate).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                        </div>
                      </div>
                    )}
                  </div>
                  
                  <p className="text-sm" style={{ fontStyle: 'italic', color: 'var(--text-2)' }}>
                    "{projection.message}"
                  </p>
                </div>
              )}

              <div className="card">
                <h2 className="heading-2" style={{ marginBottom: '16px' }}>{editingId ? 'Edit Check-in' : 'Weekly Check-in'}</h2>
                <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>
                  {editingId ? 'Updating an existing log will recalculate your progress.' : 'Log your official weight and body fat for the week.'}
                </p>
                <form onSubmit={handleWeeklySubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div className="form-row">
                    <div className="form-group" style={{ flex: 1 }}>
                      <label className="form-label">Check-in Date</label>
                      <input type="date" className="form-input" value={weeklyData.checkinDate} onChange={e => setWeeklyData({...weeklyData, checkinDate: e.target.value})} required />
                    </div>
                  </div>
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
                  <div style={{ display: 'flex', gap: '12px' }}>
                    <button type="submit" className="btn btn-primary" disabled={submitting} style={{ flex: 1 }}>
                      {submitting ? 'Processing...' : editingId ? 'Update Check-in' : 'Log Weekly Stats'}
                    </button>
                    {editingId && (
                      <button type="button" onClick={() => {
                        setEditingId(null);
                        setWeeklyData({ weightKg: '', bodyFatPercent: '', notes: '', checkinDate: new Date().toISOString().split('T')[0] });
                      }} className="btn btn-ghost">Cancel</button>
                    )}
                  </div>
                </form>
              </div>
            </div>
          )}

          {activeTab === 'daily' && (
            <div className="anim-fade-up">
              {/* Daily Meal Form (Unchanged for brevity, same as before) */}
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
                    <Tooltip contentStyle={{ background: 'var(--bg-3)', border: '1px solid var(--border)', borderRadius: '8px' }} />
                    <Line type="monotone" dataKey="weightKg" name="Weight" stroke="var(--primary-lt)" strokeWidth={3} dot={{ r: 4 }} />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            )}

            <div style={{ marginTop: '32px' }}>
              <h3 className="heading-3" style={{ marginBottom: '16px' }}>Past Check-ins</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                {[...history].reverse().map((item, i) => (
                  <div key={i} style={{ padding: '12px 0', borderBottom: '1px solid var(--border)' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                      <span className="text-sm font-bold">Week {item.weekNumber} ({item.date})</span>
                      <span className="font-bold" style={{ color: 'var(--primary-lt)' }}>{item.weightKg} kg</span>
                    </div>
                    <div style={{ display: 'flex', gap: '12px' }}>
                       <button onClick={() => startEditing(item)} className="text-xs text-accent" style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>Edit</button>
                       <button onClick={() => deleteWeeklyEntry(item.id)} className="text-xs text-red" style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}>Delete</button>
                    </div>
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
