'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { apiFetch, setToken } from '@/lib/api';
import toast from 'react-hot-toast';

export default function Login() {
  const router = useRouter();
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify(formData)
      });
      setToken(res.token);
      toast.success('Successfully logged in!');
      router.push('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Login failed');
      toast.error('Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container section" style={{ display: 'flex', justifyContent: 'center' }}>
      <div className="card anim-fade-up" style={{ width: '100%', maxWidth: '400px' }}>
        <h2 className="heading-2" style={{ marginBottom: '24px', textAlign: 'center' }}>Welcome Back</h2>
        
        {error && <div className="alert alert-error" style={{ marginBottom: '16px' }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Username</label>
            <input 
              type="text" 
              className="form-input" 
              value={formData.username}
              onChange={e => setFormData({...formData, username: e.target.value})}
              required 
            />
            <span className="text-muted text-xs" style={{ marginTop: '4px', display: 'block' }}>
              Must contain both letters and numbers (e.g. john123).
            </span>
          </div>
          
          <div className="form-group">
            <label className="form-label">Password</label>
            <div style={{ position: 'relative' }}>
              <input 
                type={showPassword ? "text" : "password"} 
                className="form-input" 
                value={formData.password}
                onChange={e => setFormData({...formData, password: e.target.value})}
                required 
                style={{ paddingRight: '40px' }}
              />
              <button 
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                style={{
                  position: 'absolute',
                  right: '12px',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  background: 'none',
                  border: 'none',
                  color: 'var(--text-2)',
                  cursor: 'pointer',
                  fontSize: '18px'
                }}
                title={showPassword ? "Hide password" : "Show password"}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <button type="submit" className="btn btn-primary" style={{ justifyContent: 'center', marginTop: '8px' }} disabled={loading}>
            {loading ? <span className="spinner"></span> : 'Log In'}
          </button>
        </form>

        <p className="text-center text-sm text-muted" style={{ marginTop: '24px', textAlign: 'center' }}>
          Don't have an account? <Link href="/auth/register" style={{ color: 'var(--primary-lt)' }}>Sign up</Link>
        </p>
      </div>
    </div>
  );
}
