'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { apiFetch } from '@/lib/api';
import toast from 'react-hot-toast';

export default function Register() {
  const router = useRouter();
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await apiFetch('/auth/register', {
        method: 'POST',
        body: JSON.stringify(formData)
      });
      toast.success('Account created successfully! Please log in.');
      router.push('/auth/login?registered=true');
    } catch (err: any) {
      setError(err.message || 'Registration failed');
      toast.error(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container section" style={{ display: 'flex', justifyContent: 'center' }}>
      <div className="card anim-fade-up" style={{ width: '100%', maxWidth: '420px' }}>
        <h2 className="heading-2" style={{ marginBottom: '8px', textAlign: 'center' }}>Create Account</h2>
        <p className="text-muted text-sm" style={{ textAlign: 'center', marginBottom: '24px' }}>
          Start your AI-powered fitness journey today.
        </p>

        {error && <div className="alert alert-error" style={{ marginBottom: '16px' }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input
              type="text"
              className="form-input"
              placeholder="e.g. john123"
              value={formData.username}
              onChange={e => setFormData({...formData, username: e.target.value})}
              required
            />
            <span className="text-muted text-xs" style={{ marginTop: '4px', display: 'block' }}>
              Must be unique and contain both letters and numbers (e.g. john123).
            </span>
          </div>

          <div className="form-group">
            <label className="form-label">Email</label>
            <input
              type="email"
              className="form-input"
              placeholder="you@example.com"
              value={formData.email}
              onChange={e => setFormData({...formData, email: e.target.value})}
              required
            />
            <span className="text-muted text-xs" style={{ marginTop: '4px', display: 'block' }}>
              You can use your email to log in as well.
            </span>
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <input
                type={showPassword ? "text" : "password"}
                className="form-input"
                placeholder="At least 6 characters"
                value={formData.password}
                onChange={e => setFormData({...formData, password: e.target.value})}
                required
                style={{ paddingRight: '40px' }}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute', right: '12px', top: '50%',
                  transform: 'translateY(-50%)', background: 'none',
                  border: 'none', color: 'var(--text-2)', cursor: 'pointer', fontSize: '18px'
                }}
                title={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
            <span className="text-muted text-xs" style={{ marginTop: '4px', display: 'block' }}>
              Must be at least 6 characters and contain both letters and numbers.
            </span>
          </div>

          <button type="submit" className="btn btn-primary" style={{ justifyContent: 'center', marginTop: '8px' }} disabled={loading}>
            {loading ? <span className="spinner"></span> : 'Create Account'}
          </button>
        </form>

        <p className="text-center text-sm text-muted" style={{ marginTop: '24px', textAlign: 'center' }}>
          Already have an account? <Link href="/auth/login" style={{ color: 'var(--primary-lt)' }}>Log in</Link>
        </p>
      </div>
    </div>
  );
}
