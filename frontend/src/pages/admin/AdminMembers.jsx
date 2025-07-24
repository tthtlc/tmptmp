
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
