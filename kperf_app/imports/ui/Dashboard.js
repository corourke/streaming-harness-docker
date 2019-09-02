import React from 'react'
import { Redirect } from 'react-router-dom'
import { Meteor } from 'meteor/meteor'
import { Container, Row, Col, Breadcrumb, Card, Button } from 'react-bootstrap'
import { withTracker } from 'meteor/react-meteor-data';

import { HealthChecks } from '../api/healthChecks.js';

import Header from './Header'
import Block from './Block'

class Dashboard extends React.Component {
  render_hc() {
    return (
      <ul>
        { this.props.healthChecks.map( (hc) => (
          <li key={hc._id}>{hc.component} status: {hc.status}</li>
        ) ) }
      </ul>
    )
  }

  render() {
    // if(Meteor.userId() == null) {
    //   return <Redirect to="/login" />
    // }
    return (
      <div>
        <Container fluid="true">
          <Row>
            <Col>
              <Header>Dashboard</Header>
            </Col>
          </Row>
          <Row>
            <Col sm={6}>
              <Card style={{ marginTop: '20px' }}>
                <Card.Body>
                  <Card.Title>Card Title</Card.Title>
                  <Card.Text>
                  Some quick example text to build on the card title and make up the bulk of
                  the card's content.
                  </Card.Text>
                  <Button variant="primary">Go somewhere</Button>
                </Card.Body>
              </Card>
            </Col>

            <Col sm={6}>
              <Card style={{ marginTop: '20px' }}>
                <Card.Body>
                  <Card.Title>Health Checks</Card.Title>
                  <Card.Body>
                  { this.render_hc() }
                  </Card.Body>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
    )
  }
}

export default withTracker(() => {
  return {
    healthChecks: HealthChecks.find({}).fetch(),
  };
})(Dashboard);
