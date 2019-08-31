import React from 'react'
import { Redirect } from 'react-router-dom'
import { Meteor } from 'meteor/meteor'
import { Container, Row, Col, Breadcrumb, Card, Button } from 'react-bootstrap'

import Header from './Header'
import Block from './Block'

export default class Dashboard extends React.Component {

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
                  <Card.Title>Card Title</Card.Title>
                  <Card.Text>
                  Some quick example text to build on the card title and make up the bulk of
                  the card's content.
                  </Card.Text>
                  <Button variant="primary">Go somewhere</Button>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
    )
  }
}
