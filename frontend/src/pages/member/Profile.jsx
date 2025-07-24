// src/pages/member/Profile.jsx
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function MemberProfile() {
  const [profile, setProfile] = useState({});
  const [formData, setFormData] = useState({});
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const data = await apiRequest('GET', '/member/profile', null, credentials);
        setProfile(data);
        setFormData(data);
      } catch (err) {
        setError(err.message);
      }
    };
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updated = await apiRequest('PUT', '/member/profile', formData, credentials);
      setProfile(updated);
      alert('Profile updated');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div>
      <h2>Profile</h2>
      {error && <p>Error: {error}</p>}
      <form onSubmit={handleSubmit}>
        <label>Name: <input name="name" value={formData.name || ''} onChange={handleChange} /></label>
        <label>Username: <input name="username" value={formData.username || ''} onChange={handleChange} /></label>
        <label>Email: <input name="email" value={formData.email || ''} onChange={handleChange} /></label>
        <label>Password: <input name="password" type="password" onChange={handleChange} placeholder="New password" /></label>
        <button type="submit">Update</button>
      </form>
    </div>
  );
}

export default MemberProfile;
