'use client';

import { useState } from 'react';
import { apiFetch, removeToken } from '@/lib/api';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';

export default function Settings() {
  const router = useRouter();
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [error, setError] = useState('');

  // Password change state
  const [pwForm, setPwForm] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [pwLoading, setPwLoading] = useState(false);
  const [showCurrentPw, setShowCurrentPw] = useState(false);
  const [showNewPw, setShowNewPw] = useState(false);

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setError('New passwords do not match.');
      return;
    }
    if (pwForm.newPassword.length < 6) {
      setError('New password must be at least 6 characters.');
      return;
    }
    setPwLoading(true);
    try {
      await apiFetch('/account/change-password', {
        method: 'POST',
        body: JSON.stringify({ currentPassword: pwForm.currentPassword, newPassword: pwForm.newPassword })
      });
      toast.success('Password changed successfully!');
      setPwForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
    } catch (err: any) {
      setError(err.message || 'Failed to change password.');
      toast.error(err.message || 'Failed to change password.');
    } finally {
      setPwLoading(false);
    }
  };

  const handleLogout = () => {
    removeToken();
    toast.success('Logged out successfully.');
    router.push('/auth/login');
  };

  const handleDeleteAccount = async () => {
    const confirmDelete = window.confirm(
      "Are you absolutely sure? This will permanently delete your account, profile, plans, and all progress. This cannot be undone."
    );
    if (!confirmDelete) return;

    setDeleteLoading(true);
    setError('');
    try {
      await apiFetch('/account/delete', { method: 'DELETE' });
      removeToken();
      toast.success('Account successfully deleted.');
      router.push('/');
    } catch (err: any) {
      setError(err.message || 'Failed to delete account.');
      toast.error('Failed to delete account.');
      setDeleteLoading(false);
    }
  };

  const eyeBtn = (show: boolean, toggle: () => void) => (
    <button type="button" onClick={toggle} style={{
      position: 'absolute', right: '12px', top: '50%',
      transform: 'translateY(-50%)', background: 'none',
      border: 'none', color: 'var(--text-2)', cursor: 'pointer', fontSize: '18px'
    }}>
      {show ? '👁️' : '👁️‍🗨️'}
    </button>
  );

  return (
    <div className="container section">
      <h1 className="heading-1" style={{ marginBottom: '32px' }}>Settings</h1>

      {error && <div className="alert alert-error" style={{ marginBottom: '24px' }}>{error}</div>}

      {/* Change Password */}
      <div className="card anim-fade-up" style={{ marginBottom: '24px' }}>
        <h2 className="heading-2" style={{ marginBottom: '8px' }}>Change Password</h2>
        <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>
          Update your password. You will remain logged in after changing it.
        </p>
        <form onSubmit={handlePasswordChange} style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Current Password</label>
            <div style={{ position: 'relative' }}>
              <input type={showCurrentPw ? 'text' : 'password'} className="form-input"
                value={pwForm.currentPassword} onChange={e => setPwForm({...pwForm, currentPassword: e.target.value})}
                required style={{ paddingRight: '40px' }} />
              {eyeBtn(showCurrentPw, () => setShowCurrentPw(!showCurrentPw))}
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label className="form-label">New Password</label>
              <div style={{ position: 'relative' }}>
                <input type={showNewPw ? 'text' : 'password'} className="form-input"
                  value={pwForm.newPassword} onChange={e => setPwForm({...pwForm, newPassword: e.target.value})}
                  required style={{ paddingRight: '40px' }} />
                {eyeBtn(showNewPw, () => setShowNewPw(!showNewPw))}
              </div>
            </div>
            <div className="form-group">
              <label className="form-label">Confirm New Password</label>
              <input type="password" className="form-input"
                value={pwForm.confirmPassword} onChange={e => setPwForm({...pwForm, confirmPassword: e.target.value})}
                required />
            </div>
          </div>
          <div>
            <button type="submit" className="btn btn-primary" disabled={pwLoading}>
              {pwLoading ? <span className="spinner"></span> : 'Update Password'}
            </button>
          </div>
        </form>
      </div>

      {/* Logout */}
      <div className="card anim-fade-up" style={{ marginBottom: '24px' }}>
        <h2 className="heading-2" style={{ marginBottom: '8px' }}>Log Out</h2>
        <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>
          Sign out of your account on this device.
        </p>
        <button onClick={handleLogout} className="btn btn-ghost">Log Out</button>
      </div>

      {/* Danger Zone */}
      <div className="card anim-fade-up" style={{ borderColor: 'rgba(239,68,68,0.3)' }}>
        <h2 className="heading-2" style={{ marginBottom: '8px', color: 'var(--red)' }}>Danger Zone</h2>
        <p className="text-muted text-sm" style={{ marginBottom: '24px' }}>
          Deleting your account is permanent. All data — profile, plans, chat history, and progress — will be erased forever.
        </p>
        <button onClick={handleDeleteAccount} className="btn btn-danger" disabled={deleteLoading}>
          {deleteLoading ? <span className="spinner"></span> : '🗑️ Delete My Account'}
        </button>
      </div>
    </div>
  );
}
