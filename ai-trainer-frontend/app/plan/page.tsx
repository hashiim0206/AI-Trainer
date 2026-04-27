'use client';

import { useEffect, useState } from 'react';
import { apiFetch } from '@/lib/api';
import { useRouter } from 'next/navigation';

import ReactMarkdown from 'react-markdown';
import toast from 'react-hot-toast';

export default function Plan() {
  const router = useRouter();
  const [goal, setGoal] = useState('');
  const [loading, setLoading] = useState(false);
  const [plan, setPlan] = useState<any>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchLatestPlan();
  }, []);

  const fetchLatestPlan = async () => {
    try {
      const data = await apiFetch('/plan/my-plan');
      setPlan(data);
    } catch (err: any) {
      // It's normal to not have a plan yet
    }
  };

  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const data = await apiFetch('/plan/generate', {
        method: 'POST',
        body: JSON.stringify({ goal })
      });
      setPlan(data);
      toast.success('Your personalized AI plan is ready!');
    } catch (err: any) {
      setError(err.message || 'Failed to generate plan');
      toast.error('Failed to generate plan. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container section">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <h1 className="heading-1">Your AI Plan</h1>
        {plan && !loading && (
          <button 
            className="btn btn-ghost" 
            onClick={() => window.print()}
            style={{ display: 'flex', alignItems: 'center', gap: '8px' }}
          >
            📄 Export to PDF
          </button>
        )}
      </div>

      {error && <div className="alert alert-error" style={{ marginBottom: '24px' }}>{error}</div>}

      <div className="card anim-fade-up" style={{ marginBottom: '32px' }}>
        <h3 className="heading-3" style={{ marginBottom: '16px' }}>What is your goal?</h3>
        <form onSubmit={handleGenerate} style={{ display: 'flex', gap: '16px' }}>
          <input 
            type="text" 
            className="form-input" 
            placeholder="e.g. I want to lose body fat and get faster for football in 3 months" 
            value={goal}
            onChange={e => setGoal(e.target.value)}
            required
            style={{ flex: 1 }}
          />
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? <span className="spinner"></span> : (plan ? 'Regenerate Plan' : 'Generate Plan')}
          </button>
        </form>
        {loading && (
          <p className="text-muted text-sm" style={{ marginTop: '12px' }}>
            Our AI is analyzing your health stats and generating a customized 7-day plan. This usually takes 10-15 seconds...
          </p>
        )}
      </div>

      {plan && !loading && (
        <div className="anim-fade-up" style={{ animationDelay: '0.2s' }}>
          <div className="card card-glow" style={{ marginBottom: '24px', background: 'var(--primary-glow)', borderColor: 'var(--primary)', textAlign: 'center' }}>
            <p className="heading-3" style={{ fontStyle: 'italic' }}>"{plan.motivationalMessage}"</p>
          </div>

          <div className="form-row">
            <div className="card markdown-content">
              <h2 className="heading-2 text-gradient" style={{ marginBottom: '24px' }}>Diet Plan</h2>
              <div style={{ fontSize: '14px', color: 'var(--text-2)' }}>
                <ReactMarkdown>{plan.dietPlan}</ReactMarkdown>
              </div>
            </div>

            <div className="card markdown-content">
              <h2 className="heading-2 text-gradient" style={{ marginBottom: '24px' }}>Workout Plan</h2>
              <div style={{ fontSize: '14px', color: 'var(--text-2)' }}>
                <ReactMarkdown>{plan.workoutPlan}</ReactMarkdown>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
