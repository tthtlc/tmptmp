// src/pages/admin/Members.jsx
import { useEffect, useState } from 'react';
import apiService from '../../services/api.js';
import { useAuth } from '../../context/AuthContext.jsx';

function AdminMembers() {
  const [members, setMembers] = useState([]);
  const [formData, setFormData] = useState({ name: '', username: '', email: '', password: '', role: 'USER' });
  const [selectedId, setSelectedId] = useState(null);
  const [error, setError] = useState('');
  const { credentials } = useAuth();

  useEffect(() => {
    fetchMembers();
  }, []);

  const fetchMembers = async () => {
    try {
      const data = await apiRequest('GET', '/admin/members', null, credentials);
      setMembers(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleAdd = async () => {
    try {
      await apiRequest('POST', '/admin/members', formData, credentials);
      fetchMembers();
      setFormData({ name: '', username: '', email: '', password: '', role: 'USER' });
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async () => {
    if (!selectedId) return;
    try {
      await apiRequest('PUT', `/admin/members/${selectedId}`, formData, credentials);
      fetchMembers();
      setSelectedId(null);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleDelete = async (id) => {
    try {
      await apiRequest('DELETE', `/admin/members/${id}`, null, credentials);
      fetchMembers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRenew = async (id) => {
    try {
      await apiRequest('PUT', `/admin/members/${id}/renew`, null, credentials);
      fetchMembers();
    } catch (err) {
      setError(err.message);
    }
  };

  const editMember = (member) => {
    setFormData({ name: member.name, username: member.username, email: member.email, password: '', role: member.role });
    setSelectedId(member.id);
  };

  return (
    <div>
      <h2>Manage Members</h2>
      {error && <p>Error: {error}</p>}
      <form onSubmit={(e) => { e.preventDefault(); selectedId ? handleUpdate() : handleAdd(); }}>
        <input name="name" value={formData.name} onChange={handleChange} placeholder="Name" />
        <input name="username" value={formData.username} onChange={handleChange} placeholder="Username" />
        <input name="email" value={formData.email} onChange={handleChange} placeholder="Email" />
        <input name="password" type="password" value={formData.password} onChange={handleChange} placeholder="Password" />
        <select name="role" value={formData.role} onChange={handleChange}>
          <option value="USER">User</option>
          <option value="ADMIN">Admin</option>
        </select>
        <button type="submit">{selectedId ? 'Update' : 'Add'}</button>
      </form>
      <table>
        <thead><tr><th>Name</th><th>Username</th><th>Email</th><th>Role</th><th>Actions</th></tr></thead>
        <tbody>
          {members.map(member => (
            <tr key={member.id}>
              <td>{member.name}</td>
              <td>{member.username}</td>
              <td>{member.email}</td>
              <td>{member.role}</td>
              <td>
                <button onClick={() => editMember(member)}>Edit</button>
                <button onClick={() => handleDelete(member.id)}>Delete</button>
                <button onClick={() => handleRenew(member.id)}>Renew Membership</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminMembers;
