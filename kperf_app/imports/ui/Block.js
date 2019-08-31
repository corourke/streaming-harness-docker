import React from 'react'
import { Redirect } from 'react-router-dom'
import { Meteor } from 'meteor/meteor'
import { Container, Row, Col, Breadcrumb, Card, Button } from 'react-bootstrap'

export default class Block extends React.Component {

  render() {
    return (
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
    )
  }
}
