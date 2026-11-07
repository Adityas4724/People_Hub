// ============================================================
// 1. IMPORTS
// React hooks and the configured API client used by this app.
// ============================================================
import { useEffect, useState } from 'react'
import api from './api'

// ============================================================
// 2. ICON DEFINITIONS
// SVG path data for the icons used in navigation and buttons.
// ============================================================
const P = {
  overview: 'M3 11l9-8 9 8M5 10v10h14V10', employees: 'M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2M9 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8M23 21v-2a4 4 0 0 0-3-3.9M16 3.1a4 4 0 0 1 0 7.8',
  departments: 'M3 21h18M5 21V7l7-4 7 4v14M9 9h1M14 9h1M9 13h1M14 13h1M10 21v-4h4v4', attendance: 'M12 22a10 10 0 1 0 0-20 10 10 0 0 0 0 20M12 6v6l4 2',
  leaves: 'M3 4h18v18H3zM16 2v4M8 2v4M3 10h18', payroll: 'M2 7h20v14H2zM16 3H6a4 4 0 0 0-4 4M17 14h.01', out: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9',
  moon: 'M21 12.8A9 9 0 1 1 11.2 3a7 7 0 0 0 9.8 9.8z', sun: 'M12 17a5 5 0 1 0 0-10 5 5 0 0 0 0 10M12 1v2M12 21v2M4.2 4.2l1.4 1.4M18.4 18.4l1.4 1.4M1 12h2M21 12h2M4.2 19.8l1.4-1.4M18.4 5.6l1.4-1.4',
  plus: 'M12 5v14M5 12h14', search: 'M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16M21 21l-4.3-4.3', x: 'M18 6L6 18M6 6l12 12', check: 'M20 6L9 17l-5-5'
}
const Icon = ({ n }) => <svg className="ic" viewBox="0 0 24 24"><path d={P[n]} /></svg>

// ============================================================
// 3. HELPER FUNCTIONS
// Format dates, times, currency, durations, names and errors.
// ============================================================
const pd = s => { const [y, m, d] = s.split('-').map(Number); return new Date(y, m - 1, d) }
const fd = s => s ? pd(s).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—'
const ft = s => s ? new Date(s).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' }) : '—'
const inr = n => '₹' + Number(n || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
const today = () => new Date().toLocaleDateString('en-CA')
const hrs = (a, b) => a && b ? ((new Date(b) - new Date(a)) / 36e5).toFixed(1) + ' h' : '—'
const span = (s, e) => Math.round((pd(e) - pd(s)) / 864e5) + 1
const ini = n => (n || '?').split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase()
const month = m => m ? new Date(m + '-01T00:00').toLocaleDateString('en-IN', { month: 'short', year: 'numeric' }) : '—'
const cap = s => s[0].toUpperCase() + s.slice(1)
const fail = (e, fb) => e.response?.data?.message || fb

// ============================================================
// 4. API ENDPOINTS AND DATA CONFIGURATION
// Defines backend routes, role-based data requirements and
// the initial values for application forms and stored data.
// ============================================================
const EP = { departments: '/departments', employees: '/employees', leaves: '/leaves', attendance: '/attendance', payroll: '/payroll', me: '/employees/me' }
const needs = admin => ({
  overview: admin ? ['departments', 'employees', 'leaves', 'attendance'] : ['me', 'leaves', 'attendance', 'payroll'],
  employees: ['employees', 'departments'], departments: ['departments', 'employees'], attendance: ['attendance'],
  leaves: ['leaves'], payroll: admin ? ['payroll', 'employees'] : ['payroll']
})
const EMPTY = { departments: [], employees: [], leaves: [], attendance: [], payroll: [], me: null }
const NEW_EMP = { fullName: '', username: '', email: '', temporaryPassword: 'Welcome123!', designation: '', phone: '', joiningDate: '', departmentId: '' }
const NEW_PAY = { employeeId: '', payMonth: '', basic: '', allowances: '0', deductions: '0', paidOn: '' }

// ============================================================
// 5. REUSABLE UI COMPONENTS
// Small components shared across dashboard tables and dialogs.
// ============================================================
const Who = ({ name, sub }) => <div className="who"><span className="avatar">{ini(name)}</span><div>{name || '—'}{sub && <small>{sub}</small>}</div></div>
const Badge = ({ s, cls }) => <span className={`badge ${cls || s}`}>{cap(s.toLowerCase())}</span>

// ------------------------------------------------------------
// 5A. TABLE COMPONENT
// Displays searchable data tables, loading placeholders and
// empty-state messages.
// ------------------------------------------------------------
function Table({ title, cols, rows, loading, empty, hint }) {
  const [q, setQ] = useState('')
  const shown = q ? rows.filter(r => JSON.stringify(r).toLowerCase().includes(q.toLowerCase())) : rows
  return (
    <div className="card">
      <div className="thead">
        <h3>{title} <span className="muted">({shown.length})</span></h3>
        <label className="search"><Icon n="search" /><input placeholder="Search" value={q} onChange={e => setQ(e.target.value)} aria-label={`Search ${title}`} /></label>
      </div>
      <div className="twrap">
        {loading && !rows.length ? [1, 2, 3, 4].map(i => <div key={i} className="skel" />) : !shown.length ? (
          <div className="empty"><b>{q ? 'No matches' : empty}</b>{q ? 'Try a different search.' : hint}</div>
        ) : (
          <table>
            <thead><tr>{cols.map(c => <th key={c.h} className={c.cls}>{c.h}</th>)}</tr></thead>
            <tbody>{shown.map((r, i) => <tr key={r.id ?? i}>{cols.map(c => <td key={c.h} className={c.cls}>{c.c(r)}</td>)}</tr>)}</tbody>
          </table>
        )}
      </div>
    </div>
  )
}

// ------------------------------------------------------------
// 5B. MODAL COMPONENT
// Displays pop-up forms and dialogs; closes on Escape or
// backdrop click.
// ------------------------------------------------------------
function Modal({ title, onClose, onSubmit, label, disabled, children }) {
  useEffect(() => { const k = e => e.key === 'Escape' && onClose(); window.addEventListener('keydown', k); return () => window.removeEventListener('keydown', k) }, [])
  return (
    <div className="scrim" onMouseDown={e => e.target === e.currentTarget && onClose()}>
      <form className="modal" onSubmit={onSubmit} role="dialog" aria-label={title}>
        <h3>{title}</h3>
        {children}
        <div className="mfoot">
          <button type="button" className="btn ghost" onClick={onClose}>Cancel</button>
          {onSubmit && <button className="btn pri" disabled={disabled}>{label}</button>}
        </div>
      </form>
    </div>
  )
}

// ============================================================
// 6. MAIN APP COMPONENT
// Holds application state and coordinates authentication,
// dashboard navigation, data loading and user actions.
// ============================================================
export default function App() {
  const [token, setToken] = useState(localStorage.getItem('peoplehub_token'))
  const [user, setUser] = useState(localStorage.getItem('peoplehub_user') || '')
  const [role, setRole] = useState(localStorage.getItem('peoplehub_role') || '')
  const [login, setLogin] = useState({ username: 'admin', password: '' })
  const [loginErr, setLoginErr] = useState('')
  const [busy, setBusy] = useState(false)
  const [section, setSection] = useState('overview')
  const [store, setStore] = useState(EMPTY)
  const [loading, setLoading] = useState(false)
  const [toast, setToast] = useState(null)
  const [form, setForm] = useState(null)
  const [confirm, setConfirm] = useState(null)
  const [deptName, setDeptName] = useState('')
  const [theme, setTheme] = useState(localStorage.getItem('peoplehub_theme') || 'light')
  const admin = role === 'ADMIN'

  // ----------------------------------------------------------
  // 6A. GLOBAL EFFECTS
  // Applies the selected theme, clears notifications after a
  // delay, and loads data when the active section changes.
  // ----------------------------------------------------------
  useEffect(() => { document.documentElement.dataset.theme = theme; localStorage.setItem('peoplehub_theme', theme) }, [theme])
  useEffect(() => { if (toast) { const t = setTimeout(() => setToast(null), 4500); return () => clearTimeout(t) } }, [toast])
  useEffect(() => { if (token) load() }, [section, token])

  const notify = (text, type = 'ok') => setToast({ text, type })

  // ----------------------------------------------------------
  // 6B. LOAD DASHBOARD DATA
  // Requests the data needed for the current section and stores
  // successful responses in application state.
  // ----------------------------------------------------------
  async function load() {
    const keys = needs(admin)[section]
    setLoading(true)
    const res = await Promise.allSettled(keys.map(k => api.get(EP[k])))
    setStore(s => {
      const n = { ...s }
      keys.forEach((k, i) => {
        const ok = res[i].status === 'fulfilled', v = ok && res[i].value.data
        n[k] = k === 'me' ? (v || null) : Array.isArray(v) ? v : []
      })
      return n
    })
    const bad = res.find(r => r.status === 'rejected')
    if (bad) notify(fail(bad.reason, 'Could not load this section. Check your permissions or profile.'), 'err')
    setLoading(false)
  }

  // ----------------------------------------------------------
  // 6C. SHARED ACTION AND NOTIFICATION HANDLERS
  // Runs API actions, displays success/error messages and
  // refreshes the current section.
  // ----------------------------------------------------------
  async function run(fn, ok, bad) {
    try { await fn(); notify(ok); await load(); return true } catch (e) { notify(fail(e, bad), 'err'); return false }
  }

  // ----------------------------------------------------------
  // 6D. AUTHENTICATION
  // Signs in through the backend and saves the returned
  // token, username and role in local storage.
  // ----------------------------------------------------------
  async function signIn(e) {
    e.preventDefault(); setBusy(true); setLoginErr('')
    try {
      const { data } = await api.post('/auth/login', login)
      localStorage.setItem('peoplehub_token', data.token); localStorage.setItem('peoplehub_user', data.username); localStorage.setItem('peoplehub_role', data.role)
      setUser(data.username); setRole(data.role); setSection('overview'); setToken(data.token)
    } catch { setLoginErr('Sign-in failed. Check your username and password, and that the backend is running.') }
    setBusy(false)
  }

  // ----------------------------------------------------------
  // 6E. SIGN OUT
  // Clears saved login information and resets app state.
  // ----------------------------------------------------------
  function signOut() {
    ['token', 'user', 'role'].forEach(k => localStorage.removeItem('peoplehub_' + k))
    setToken(null); setUser(''); setRole(''); setStore(EMPTY); setForm(null)
  }

  // ----------------------------------------------------------
  // 6F. CRUD AND WORKFLOW HANDLERS
  // Handles attendance actions, leave decisions, deletion,
  // department creation and employee editing.
  // ----------------------------------------------------------
  const act = (path, ok) => run(() => api.post(path), ok, 'Action failed.')
  const decide = (id, s) => run(() => api.patch(`/leaves/${id}/decision?status=${s}`), `Leave ${s.toLowerCase()}.`, `Could not ${s.toLowerCase()} the leave request.`)
  const ask = (text, onYes) => setConfirm({ text, onYes })
  const delDept = d => ask(`Delete the ${d.name} department? This only works if no employees are assigned to it.`, () => run(() => api.delete(`/departments/${d.id}`), 'Department deleted.', 'Could not delete this department. It may still have employees.'))
  const delEmp = e => ask(`Delete ${e.fullName}? Their login will be deactivated; attendance, leave and payroll history are kept.`, () => run(() => api.delete(`/employees/${e.id}`), 'Employee deactivated.', 'Could not delete this employee.'))
  const addDept = async e => { e.preventDefault(); if (await run(() => api.post('/departments', { name: deptName, description: '' }), 'Department added.', 'Could not add department.')) setDeptName('') }
  const editEmp = e => setForm({ type: 'emp-edit', id: e.id, title: `Edit ${e.fullName}`, v: { fullName: e.fullName || '', designation: e.designation || '', phone: e.phone || '', joiningDate: e.joiningDate || '', departmentId: e.department?.id ? String(e.department.id) : '' } })
  const open = (type, title, v) => setForm({ type, title, v })
  const set = (k, val) => setForm(f => ({ ...f, v: { ...f.v, [k]: val } }))

  // ----------------------------------------------------------
  // 6G. FORM SUBMISSION
  // Sends employee, leave and payroll form data to the backend.
  // ----------------------------------------------------------
  async function submit(e) {
    e.preventDefault()
    const { type, v } = form, dep = v.departmentId ? Number(v.departmentId) : null
    const call = {
      'emp-new': () => run(() => api.post('/employees', { ...v, departmentId: dep, joiningDate: v.joiningDate || null }), 'Employee account created. Share the temporary password securely.', 'Could not create employee.'),
      'emp-edit': () => run(() => api.put(`/employees/${form.id}`, { ...v, departmentId: dep, joiningDate: v.joiningDate || null }), 'Employee updated.', 'Could not update employee.'),
      leave: () => run(() => api.post('/leaves', v), 'Leave request submitted.', 'Could not submit leave request.'),
      pay: () => run(() => api.post('/payroll', { employeeId: Number(v.employeeId), payMonth: v.payMonth, basic: Number(v.basic), allowances: Number(v.allowances || 0), deductions: Number(v.deductions || 0), paidOn: v.paidOn || null }), 'Payroll record saved.', 'Could not save payroll.')
    }[type]
    if (await call()) setForm(null)
  }

  // ==========================================================
  // 7. LOGIN SCREEN
  // Shown when no authentication token is stored.
  // ==========================================================
  if (!token) return (
    <div className="login">
      <aside className="login-art">
        <div className="brand"><span className="mark">P</span><b>PeopleHub</b></div>
        <div>
          <h2>Your whole team, one calm workspace.</h2>
          <p>Attendance, leave and payroll without the spreadsheets.</p>
        </div>
        <ul>{['Check in and out in one tap', 'Approve leave in seconds', 'Payslips always up to date'].map(t => <li key={t}><Icon n="check" />{t}</li>)}</ul>
      </aside>
      <main className="login-form">
        <form onSubmit={signIn}>
          <div><h1>Sign in</h1><p className="muted">Use the account your admin gave you.</p></div>
          <label className="field">Username<input value={login.username} onChange={e => setLogin({ ...login, username: e.target.value })} autoComplete="username" required /></label>
          <label className="field">Password<input type="password" value={login.password} onChange={e => setLogin({ ...login, password: e.target.value })} autoComplete="current-password" required /></label>
          {loginErr && <p className="badge REJECTED" style={{ borderRadius: 10, padding: '10px 12px', fontWeight: 500 }}>{loginErr}</p>}
          <button className="btn pri" style={{ justifyContent: 'center', padding: 13 }} disabled={busy}>{busy ? 'Signing in…' : 'Sign in'}</button>
        </form>
      </main>
    </div>
  )

  // ==========================================================
  // 8. DASHBOARD DATA AND DISPLAY CONFIGURATION
  // Prepares role-based navigation, dashboard statistics,
  // greeting text, table columns and section headings.
  // ==========================================================
  const { departments, employees, leaves, attendance, payroll, me } = store
  const nav = admin ? ['overview', 'employees', 'departments', 'attendance', 'leaves', 'payroll'] : ['overview', 'attendance', 'leaves', 'payroll']
  const rec = attendance.find(a => a.workDate === today())
  const pending = leaves.filter(l => l.status === 'PENDING')
  const present = attendance.filter(a => a.workDate === today() && a.checkIn).length
  const hr = new Date().getHours(), hello = hr < 12 ? 'Good morning' : hr < 17 ? 'Good afternoon' : 'Good evening'
  const dateText = new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long' })
  const status = !rec?.checkIn ? 'Not checked in' : !rec.checkOut ? `In since ${ft(rec.checkIn)}` : `Done · ${hrs(rec.checkIn, rec.checkOut)}`
  const sorted = [...attendance].sort((a, b) => (b.workDate || '').localeCompare(a.workDate || ''))
  const empCol = { h: 'Employee', c: r => <Who name={r.employee?.fullName} sub={r.employee?.employeeCode} /> }
  const adminCol = admin ? [empCol] : []
  const meta = {
    overview: ['Dashboard', dateText], employees: ['Employees', 'Create and manage employee accounts'], departments: ['Departments', 'Organise your teams'],
    attendance: ['Attendance', admin ? 'Daily check-in records for everyone' : 'Your daily check-in history'],
    leaves: ['Leave', admin ? 'Review and decide on requests' : 'Your leave requests'], payroll: ['Payroll', admin ? 'Monthly salary records' : 'Your payslips']
  }[section]

  // ----------------------------------------------------------
  // 8A. DASHBOARD ACTION BUTTONS
  // Defines check-in/check-out and leave approval controls.
  // ----------------------------------------------------------
  const checkBtns = <div className="hero-a">
    <button className="btn" disabled={!!rec?.checkIn} onClick={() => act('/attendance/check-in', 'Checked in.')}>Check in</button>
    <button className="btn ghost" disabled={!rec?.checkIn || !!rec?.checkOut} onClick={() => act('/attendance/check-out', 'Checked out.')}>Check out</button>
  </div>
  const decideBtns = l => l.status === 'PENDING'
    ? <div className="acts"><button className="btn sm ok" onClick={() => decide(l.id, 'APPROVED')}>Approve</button><button className="btn sm bad" onClick={() => decide(l.id, 'REJECTED')}>Reject</button></div>
    : <span className="muted">Decided</span>

  // ----------------------------------------------------------
  // 8B. FORM FIELD HELPERS AND PAY CALCULATIONS
  // Builds reusable form inputs and calculates gross/net pay.
  // ----------------------------------------------------------
  const v = form?.v || {}
  const F = (k, label, { full, ...p } = {}) => <label key={k} className={'field' + (full ? ' span2' : '')}>{label}<input value={v[k] ?? ''} onChange={e => set(k, e.target.value)} {...p} /></label>
  const Dep = <label className="field">Department<select value={v.departmentId} onChange={e => set('departmentId', e.target.value)}><option value="">No department</option>{departments.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}</select></label>
  const gross = Number(v.basic || 0) + Number(v.allowances || 0), net = gross - Number(v.deductions || 0)

  // ==========================================================
  // 9. MAIN APPLICATION LAYOUT
  // Renders the sidebar, header, dashboard sections and dialogs.
  // ==========================================================
  return (
    <>
      <aside className="sidebar">
        <div className="brand"><span className="mark">P</span><b>PeopleHub</b></div>
        {nav.map(n => <button key={n} className={'nav' + (section === n ? ' on' : '')} onClick={() => setSection(n)} aria-label={cap(n)}><Icon n={n} /><span>{cap(n)}</span></button>)}
        <div className="me">
          <span className="avatar">{ini(user)}</span>
          <div style={{ flex: 1 }}><b>{user}</b><small>{role.toLowerCase()}</small></div>
          <button className="btn" onClick={signOut} aria-label="Sign out"><Icon n="out" /></button>
        </div>
      </aside>

      <main className="content">
        <header className="top">
          <div><h1>{meta[0]}</h1><p className="muted">{meta[1]}</p></div>
          <div className="top-r">
            {section === 'employees' && admin && <button className="btn pri" onClick={() => open('emp-new', 'Create employee', NEW_EMP)}><Icon n="plus" />Add employee</button>}
            {section === 'leaves' && !admin && <button className="btn pri" onClick={() => open('leave', 'Apply for leave', { startDate: '', endDate: '', reason: '' })}><Icon n="plus" />Apply for leave</button>}
            {section === 'payroll' && admin && <button className="btn pri" onClick={() => open('pay', 'Add payroll record', NEW_PAY)}><Icon n="plus" />Add payroll</button>}
            {section === 'attendance' && !admin && <>
              <button className="btn pri" disabled={!!rec?.checkIn} onClick={() => act('/attendance/check-in', 'Checked in.')}>Check in</button>
              <button className="btn" disabled={!rec?.checkIn || !!rec?.checkOut} onClick={() => act('/attendance/check-out', 'Checked out.')}>Check out</button>
            </>}
            <button className="btn ghost" onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')} aria-label="Toggle dark mode"><Icon n={theme === 'dark' ? 'sun' : 'moon'} /></button>
          </div>
        </header>

                {/* ----------------------------------------------------
            9A. OVERVIEW / DASHBOARD
            Shows summary cards, leave activity, department
            headcount or the employee's personal profile.
        ----------------------------------------------------- */}
        {section === 'overview' && <>
          <div className="hero">
            <div><h2>{hello}, {me?.fullName?.split(' ')[0] || user}</h2><p>{admin ? `${present} of ${employees.length} employees have checked in today.` : `Today: ${status}`}</p></div>
            {admin ? <div className="hero-a"><button className="btn" onClick={() => { setSection('employees'); open('emp-new', 'Create employee', NEW_EMP) }}>Add employee</button><button className="btn ghost" onClick={() => setSection('leaves')}>Review leave</button></div> : checkBtns}
          </div>
          <div className={'grid ' + (admin ? 'g4' : 'g3')}>
            {(admin ? [['employees', 'Employees', employees.length], ['departments', 'Departments', departments.length], ['leaves', 'Pending leave', pending.length], ['attendance', 'Present today', present]]
              : [['attendance', 'Today', status], ['leaves', 'Pending leave', pending.length], ['payroll', 'Latest net pay', payroll[0] ? inr(payroll[0].netPay) : '—']]
            ).map(([i, l, n]) => <div key={l} className="card stat"><span className="ico"><Icon n={i} /></span><div><span>{l}</span><strong style={typeof n === 'string' ? { fontSize: 17 } : null}>{n}</strong></div></div>)}
          </div>
          <div className="grid g2">
            <div className="card">
              <h3>{admin ? 'Leave awaiting your decision' : 'Recent leave requests'}</h3>
              {(admin ? pending : leaves).slice(0, 5).map(l => <div className="row" key={l.id}>
                {admin && <span className="avatar">{ini(l.employee?.fullName)}</span>}
                <div className="grow">{admin ? l.employee?.fullName : `${fd(l.startDate)} – ${fd(l.endDate)}`}<small>{admin ? `${fd(l.startDate)} – ${fd(l.endDate)} · ${span(l.startDate, l.endDate)} day(s)` : l.reason}</small></div>
                {admin ? decideBtns(l) : <Badge s={l.status} />}
              </div>)}
              {!(admin ? pending : leaves).length && <div className="empty"><b>{admin ? 'All caught up' : 'No leave requests yet'}</b>{admin ? 'No requests need a decision.' : 'Apply for leave from the Leave page.'}</div>}
            </div>
            <div className="card">
              <h3>{admin ? 'Headcount by department' : 'My profile'}</h3>
              {admin ? departments.map(d => { const c = employees.filter(e => e.department?.id === d.id).length; return <div key={d.id} style={{ padding: '8px 0', fontSize: 13 }}><div style={{ display: 'flex', justifyContent: 'space-between' }}>{d.name}<b>{c}</b></div><div className="bar"><i style={{ width: `${employees.length ? c / employees.length * 100 : 0}%` }} /></div></div> })
                : me ? [['Employee ID', me.employeeCode], ['Designation', me.designation || '—'], ['Department', me.department?.name || '—'], ['Joined', fd(me.joiningDate)]].map(([k, val]) => <div className="row" key={k}><div className="grow muted">{k}</div><b>{val}</b></div>)
                  : <div className="empty"><b>No profile found</b>Ask your admin to complete your employee profile.</div>}
              {admin && !departments.length && <div className="empty"><b>No departments</b>Add one from the Departments page.</div>}
            </div>
          </div>
        </>}

                {/* ----------------------------------------------------
            9B. EMPLOYEES SECTION
            Lists employees and provides edit/delete actions.
        ----------------------------------------------------- */}
        {section === 'employees' && <Table title="All employees" loading={loading} rows={employees} empty="No employees yet" hint="Use Add employee to create the first account." cols={[
          { h: 'Employee', c: r => <Who name={r.fullName} sub={r.employeeCode} /> }, { h: 'Designation', c: r => r.designation || '—' },
          { h: 'Department', c: r => r.department?.name || '—' }, { h: 'Phone', c: r => r.phone || '—' }, { h: 'Joined', c: r => fd(r.joiningDate) },
          { h: 'Actions', c: r => <div className="acts"><button className="btn sm" onClick={() => editEmp(r)}>Edit</button><button className="btn sm bad" onClick={() => delEmp(r)}>Delete</button></div> }]} />}

                {/* ----------------------------------------------------
            9C. DEPARTMENTS SECTION
            Allows admins to add departments and view or delete
            existing departments.
        ----------------------------------------------------- */}
        {section === 'departments' && <>
          {admin && <form className="inline" onSubmit={addDept}><input placeholder="New department name" value={deptName} onChange={e => setDeptName(e.target.value)} required aria-label="Department name" /><button className="btn pri">Add department</button></form>}
          <Table title="All departments" loading={loading} rows={departments} empty="No departments yet" hint="Add your first department above." cols={[
            { h: 'Department', c: r => <b>{r.name}</b> }, { h: 'Description', c: r => r.description || '—' },
            { h: 'Employees', c: r => employees.filter(e => e.department?.id === r.id).length },
            ...(admin ? [{ h: 'Actions', c: r => <button className="btn sm bad" onClick={() => delDept(r)}>Delete</button> }] : [])]} />
        </>}

                {/* ----------------------------------------------------
            9D. ATTENDANCE SECTION
            Displays employee attendance dates, check-in/out
            times, worked hours and attendance status.
        ----------------------------------------------------- */}
        {section === 'attendance' && <Table title="Attendance records" loading={loading} rows={sorted} empty="No attendance yet" hint={admin ? 'Records appear when employees check in.' : 'Check in to start today.'} cols={[
          ...adminCol, { h: 'Date', c: r => fd(r.workDate) }, { h: 'Check in', c: r => ft(r.checkIn) }, { h: 'Check out', c: r => ft(r.checkOut) }, { h: 'Hours', c: r => hrs(r.checkIn, r.checkOut) },
          { h: 'Status', c: r => r.checkOut ? <Badge s="Complete" cls="good" /> : r.checkIn ? <Badge s="Working" cls="warn" /> : <Badge s="Absent" cls="idle" /> }]} />}

                {/* ----------------------------------------------------
            9E. LEAVE MANAGEMENT SECTION
            Displays leave requests and admin decision controls.
        ----------------------------------------------------- */}
        {section === 'leaves' && <Table title="Leave requests" loading={loading} rows={leaves} empty="No leave requests" hint={admin ? 'Requests from employees will show here.' : 'Use Apply for leave to submit one.'} cols={[
          ...adminCol, { h: 'From', c: r => fd(r.startDate) }, { h: 'To', c: r => fd(r.endDate) }, { h: 'Days', c: r => span(r.startDate, r.endDate) },
          { h: 'Reason', cls: 'wrapcell', c: r => r.reason }, { h: 'Status', c: r => <Badge s={r.status} /> }, ...(admin ? [{ h: 'Decision', c: decideBtns }] : [])]} />}

                {/* ----------------------------------------------------
            9F. PAYROLL SECTION
            Displays monthly salary details and payment status.
        ----------------------------------------------------- */}
        {section === 'payroll' && <Table title="Payroll records" loading={loading} rows={payroll} empty="No payroll records" hint={admin ? 'Add a record for an employee to begin.' : 'Your payslips will appear here.'} cols={[
          ...adminCol, { h: 'Month', c: r => month(r.payMonth) }, { h: 'Basic', cls: 'num', c: r => inr(r.basic) }, { h: 'Allowances', cls: 'num', c: r => inr(r.allowances) },
          { h: 'Deductions', cls: 'num', c: r => inr(r.deductions) }, { h: 'Net pay', cls: 'num', c: r => <b>{inr(r.netPay)}</b> },
          { h: 'Paid on', c: r => r.paidOn ? fd(r.paidOn) : <Badge s="Unpaid" cls="warn" /> }]} />}
      </main>

            {/* ------------------------------------------------------
          10. CREATE / EDIT FORMS
          Renders the correct employee, leave or payroll form
          inside the reusable modal component.
      ------------------------------------------------------- */}
      {form && <Modal title={form.title} onClose={() => setForm(null)} onSubmit={submit} label={{ 'emp-new': 'Create employee', 'emp-edit': 'Save changes', leave: 'Submit request', pay: 'Save payroll' }[form.type]} disabled={form.type === 'pay' && (!v.employeeId || net < 0)}>
        <div className="fgrid">
          {form.type === 'emp-new' && <>{F('fullName', 'Full name', { required: true })}{F('username', 'Username', { required: true })}{F('email', 'Email', { type: 'email', required: true })}{F('temporaryPassword', 'Temporary password', { required: true })}</>}
          {form.type === 'emp-edit' && F('fullName', 'Full name', { required: true, full: true })}
          {form.type.startsWith('emp') && <>{F('designation', 'Designation')}{F('phone', 'Phone')}{F('joiningDate', 'Joining date', { type: 'date' })}{Dep}</>}
          {form.type === 'leave' && <>{F('startDate', 'Start date', { type: 'date', required: true, min: today() })}{F('endDate', 'End date', { type: 'date', required: true, min: v.startDate || today() })}
            <label className="field span2">Reason<textarea value={v.reason} onChange={e => set('reason', e.target.value)} required /></label></>}
          {form.type === 'pay' && <>
            <label className="field span2">Employee<select required value={v.employeeId} onChange={e => set('employeeId', e.target.value)}><option value="">Select employee</option>{employees.map(x => <option key={x.id} value={x.id}>{x.employeeCode} · {x.fullName}</option>)}</select></label>
            {F('payMonth', 'Pay month', { type: 'month', required: true })}{F('paidOn', 'Paid on', { type: 'date' })}
            {F('basic', 'Basic salary (₹)', { type: 'number', min: 0, step: '0.01', required: true })}{F('allowances', 'Allowances (₹)', { type: 'number', min: 0, step: '0.01' })}{F('deductions', 'Deductions (₹)', { type: 'number', min: 0, step: '0.01' })}
            <div className={'net span2'} style={net < 0 ? { background: 'var(--bad-bg)', color: 'var(--bad)' } : null}>{net < 0 ? 'Deductions cannot exceed gross pay' : `Net salary ${inr(net)}`}</div></>}
        </div>
      </Modal>}

            {/* ------------------------------------------------------
          11. CONFIRMATION DIALOG AND TOAST NOTIFICATIONS
          Confirms destructive actions and displays feedback.
      ------------------------------------------------------- */}
      {confirm && <Modal title="Are you sure?" onClose={() => setConfirm(null)} onSubmit={e => { e.preventDefault(); confirm.onYes(); setConfirm(null) }} label="Yes, delete"><p className="muted">{confirm.text}</p></Modal>}
      {toast && <div className={'toast' + (toast.type === 'err' ? ' err' : '')} role="status">{toast.text}<button onClick={() => setToast(null)} aria-label="Dismiss"><Icon n="x" /></button></div>}
    </>
  )
}
