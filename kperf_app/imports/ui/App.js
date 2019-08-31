import React from 'react'
import { BrowserRouter as Router, Redirect, Route, Switch, Link } from 'react-router-dom'
import { Container, Row, Col } from 'react-bootstrap'

import NotFound from './NotFound'
import Dashboard from './Dashboard'

export default class App extends React.Component {

  render() {
    return (
      <Router>
              <Switch>
                <Redirect exact from="/" to="/dashboard" />
                <Route path="/dashboard" component={Dashboard} />
                <Route component={NotFound} />
              </Switch>
      </Router>
    )
  }
}



// <Grid>
//   <Col sm={12} smOffset={0} md={10} mdOffset={1} lg={8} lgOffset={2}>
//

//   </Col>
// </Grid>
